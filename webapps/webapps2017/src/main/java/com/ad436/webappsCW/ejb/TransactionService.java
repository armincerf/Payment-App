/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ad436.webappsCW.ejb;

import com.ad436.webappsCW.entity.Notification;
import com.ad436.webappsCW.entity.SystemUser;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 * Handles transactions, notifications and user balance operations
 *
 * @author ad436
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TransactionService {

    @PersistenceContext
    EntityManager em;

    public TransactionService() {
    }

    /**
     * Uses the Restful service to return a conversion given two currencies and
     * a value
     *
     * @param currency1 the currency to convert from.
     * @param currency2 the currency to convert to.
     * @param amount the amount to convert
     * @return the converted amount
     */
    public String getConversion(String currency1, String currency2, BigDecimal amount) {
        Client client = ClientBuilder.newClient();
        String result = client.target("http://localhost:8080/webapps2017/rest/conversion/" + currency1 + "/" + currency2 + "/" + amount)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
        System.out.println(result);
        return result;
    }

    /**
     * Queries the database for the user matching the given username
     *
     * @param username
     * @return the SystemUser object matching the username
     */
    public SystemUser getUser(String username) {
        return (SystemUser) em.createNamedQuery("findAllUsers")
                .setParameter("username", username)
                .getSingleResult();
    }

    /**
     * Only called if the transaction has been validated Deducts the amount from
     * the senders account and adds the converted amount to the recipients Then
     * creates a record of the transaction in the database before finalising the
     * transaction
     *
     * @param senderUsername
     * @param recipientUsername
     * @param amount
     */
    public void makeTransaction(String senderUsername, String recipientUsername, BigDecimal amount) {
        SystemUser sender = getUser(senderUsername);
        SystemUser receiver = getUser(recipientUsername);

        BigDecimal debitAmount = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal creditAmount = new BigDecimal(getConversion(sender.getCurrency(), receiver.getCurrency(), amount).substring(3), new MathContext(2, RoundingMode.HALF_UP));

        receiver.setBalance(receiver.getBalance().add(creditAmount));
        sender.setBalance(sender.getBalance().subtract(debitAmount));

        String currency = sender.getCurrency();
        Notification newTransaction = new Notification(sender, receiver, currency, amount.setScale(2, RoundingMode.HALF_UP), sender.getBalance(), receiver.getBalance(), new Date(), false, false, false);
        em.persist(newTransaction);
        em.flush();

        System.out.println("Transaction success: " + amount.setScale(2, RoundingMode.HALF_UP) + " transfered from " + sender.getName() + " to " + receiver.getName());
    }

    /**
     * Creates a new notification object and inserts it to the database
     *
     * @param senderUsername
     * @param recipientUsername
     * @param amount
     */
    public void setNotification(String senderUsername, String recipientUsername, BigDecimal amount) {
        System.out.println(amount);
        SystemUser sender = getUser(senderUsername);
        SystemUser receiver = getUser(recipientUsername);

        Notification notification = new Notification(sender, receiver, sender.getCurrency(), amount.setScale(2, RoundingMode.HALF_UP), sender.getBalance(), receiver.getBalance(), new Date(), false, true, false);
        em.persist(notification);
        em.flush();
    }

    /**
     * Checks the senders account to confirm they have enough funds
     *
     * @param senderUsername
     * @param amount
     * @return True if the sender has enough funds to make a transaction
     */
    public boolean validateTransaction(String senderUsername, BigDecimal amount) {

        return getUser(senderUsername).getBalance().subtract(amount).doubleValue() >= 0;
    }

    /**
     * Checks the user exists in the database
     *
     * @param username
     * @return true if the user exists
     */
    public boolean checkUser(String username) {
        try {
            System.out.println("User " + getUser(username).getUsername() + " exists.");
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    /**
     * returns the Users current balance
     *
     * @param username
     * @return Users balance
     */
    public BigDecimal getBalance(String username) {
        SystemUser user = getUser(username);
        return user.getBalance();
    }

    /**
     * Finds the specified notification, sets it as 'seen' and 'rejected
     *
     * @param id id of the notification
     * @param username
     */
    public void rejectNotification(Long id, String username) {
        List<Notification> notifications = em.createNamedQuery("getReceivedNotifications")
                .setParameter("username", username)
                .getResultList();
        for (Notification n : notifications) {
            if (n.getId().equals(id)) {
                n.setRejected(true);
                n.setSeen(false);
            }
        }
    }

    /**
     * Get a list of all notifications sent to the user
     *
     * @param recipientUsername
     * @return A notification list of all notifications sent to a user
     */
    public List<Notification> getSentNotifications(String recipientUsername) {
        try {

            List<Notification> notifications = em.createNamedQuery("getSentNotifications")
                    .setParameter("username", recipientUsername)
                    .getResultList();
            return notifications;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * returns a list of of all notifications received by the user, setting each
     * one to 'read'
     *
     * @param recipientUsername
     * @return a list of of all notifications received by the user
     */
    public List<Notification> getReceivedNotifications(String recipientUsername) {
        try {

            List<Notification> notifications = em.createNamedQuery("getReceivedNotifications")
                    .setParameter("username", recipientUsername)
                    .getResultList();
            for (Notification n : notifications) {
                n.setSeen(true);
            }
            return notifications;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * Returns the amount of unread notifications for a user
     *
     * @param username
     * @return amount of unread notifications
     */
    public int getNewNotificationNo(String username) {
        int i = 0;
        List<Notification> notifications = em.createNamedQuery("getReceivedNotifications")
                .setParameter("username", username)
                .getResultList();
        for (Notification n : notifications) {
            if (!n.isSeen()) {
                i++;
            }
        }
        return i;
    }

    /**
     * Returns the amount of unread transactions for a user
     *
     * @param username
     * @return amount of unread transactions
     */
    public int getNewTransactionNo(String username) {
        int i = 0;
        List<Notification> transactions = em.createNamedQuery("getReceivedTransactionsForUser")
                .setParameter("username", username)
                .getResultList();
        for (Notification n : transactions) {
            if (!n.isSeen()) {
                i++;
            }
        }
        return i;
    }

    /**
     * returns a list of transactions received by the user
     *
     * @param username
     * @return a list of transactions received by the user
     */
    public List<Notification> getReceivedTransactions(String username) {
        try {

            List<Notification> transactions = em.createNamedQuery("getReceivedTransactionsForUser")
                    .setParameter("username", username)
                    .getResultList();
            for (Notification n : transactions) {
                n.setSeen(true);
            }
            return transactions;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * returns a list of transactions sent to the user
     *
     * @param username
     * @return a list of transactions sent to the user
     */
    public List<Notification> getSentTransactions(String username) {
        try {

            List<Notification> transactions = em.createNamedQuery("getSentTransactionsForUser")
                    .setParameter("username", username)
                    .getResultList();

            return transactions;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * returns all transactions
     *
     * @return a list of all transactions
     */
    public List<Notification> getAllTransactions() {
        List<Notification> transactions = em.createNamedQuery("getAllTransactions")
                .getResultList();
        return transactions;
    }
}
