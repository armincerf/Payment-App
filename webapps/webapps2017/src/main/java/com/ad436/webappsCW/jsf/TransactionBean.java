/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ad436.webappsCW.jsf;

import com.ad436.webappsCW.ejb.TransactionService;
import com.ad436.webappsCW.entity.Notification;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Handles transactions
 *
 * @author ad436
 */
@Named
@RequestScoped
@DeclareRoles({"Admin", "User"})
public class TransactionBean {

    @EJB
    TransactionService ts;

    String recipientUsername;
    BigDecimal amount;

    public TransactionBean() {

    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Converts an amount from currency1 to currency 2
     *
     * @param currency1
     * @param currency2
     * @param amount
     * @return A string with the converted currency code followed by the amount
     */
    public String convert(String currency1, String currency2, BigDecimal amount) {
        return ts.getConversion(currency1, currency2, amount);
    }

    /**
     * removes the currency code from the 'convert' method
     *
     * @param currency1
     * @param currency2
     * @param amount
     * @return the converted amount
     */
    public BigDecimal parseConvert(String currency1, String currency2, BigDecimal amount) {
        return new BigDecimal(convert(currency1, currency2, amount).substring(3));
    }

    /**
     * Checks the recipient user exists, the sender has enough funds, and the
     * requested amount is positive If all checks pass, make the transaction and
     * add a message with the users new balance
     */
    public void makeTransaction() {
        FacesContext context = FacesContext.getCurrentInstance();
        String senderUsername = context.getExternalContext().getRemoteUser();

        if (ts.checkUser(recipientUsername) && !(senderUsername.equals(recipientUsername))) {//recipient exists and isnt self
            if (ts.validateTransaction(senderUsername, amount)) { //user has enough funds
                if (amount.doubleValue() < 0.01) { //transaction not negative
                    context.addMessage(null, new FacesMessage("Transaction fail: Please enter a positive number"));
                } else {
                    ts.makeTransaction(senderUsername, recipientUsername, amount);
                    context.addMessage(null, new FacesMessage("Transaction success"));
                    context.addMessage(null, new FacesMessage("You now have " + ts.getUser(senderUsername).getCurrency() + getBalance() + " in your account."));
                }
            } else {
                context.addMessage(null, new FacesMessage("Transaction fail: Insuficiant funds"));
                context.addMessage(null, new FacesMessage("You have " + ts.getUser(senderUsername).getCurrency() + getBalance() + " in your account."));

            }
        } else {
            context.addMessage(null, new FacesMessage("Transaction fail: please check the username"));
        }
    }

    /**
     * Makes a transaction outside of a form (e.g from the request page)
     *
     * @param username the user receiving the transaction
     * @param amount the amount to send
     */
    public void makeTransaction(String username, BigDecimal amount) {
        FacesContext context = FacesContext.getCurrentInstance();
        String senderUsername = context.getExternalContext().getRemoteUser();

        if (ts.checkUser(username) && !(username.equals(senderUsername))) {
            if (ts.validateTransaction(senderUsername, amount)) {
                ts.makeTransaction(senderUsername, username, amount);
                context.addMessage(null, new FacesMessage("Transaction success"));
                context.addMessage(null, new FacesMessage("You now have " + ts.getUser(senderUsername).getCurrency() + getBalance() + " in your account."));
            } else {
                context.addMessage(null, new FacesMessage("Transaction fail: Insuficiant funds"));
                context.addMessage(null, new FacesMessage("You have " + ts.getUser(senderUsername).getCurrency() + getBalance() + " in your account."));

            }
        } else {
            context.addMessage(null, new FacesMessage("Transaction fail: Please check the username"));
        }
    }

    /**
     * Checks the recipient user exists and the requested amount is positive If
     * all checks pass, make a request
     */
    public void makeRequest() {
        FacesContext context = FacesContext.getCurrentInstance();
        String senderUsername = context.getExternalContext().getRemoteUser();

        if (ts.checkUser(recipientUsername)) {

            if (amount.doubleValue() < 0.01) {
                context.addMessage(null, new FacesMessage("Please enter a positive number"));
            } else {
                ts.setNotification(senderUsername, recipientUsername, amount);
                context.addMessage(null, new FacesMessage("Request sent"));
            }
        } else {
            context.addMessage(null, new FacesMessage("Request fail: user not found"));
        }
    }

    /**
     * rejects a notification
     *
     * @param id the id of the notification to decline
     */
    public void declineNotification(Long id) {
        ts.rejectNotification(id, FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Transaction declined"));
    }

    /**
     * return the balance of the current user
     *
     * @return the users balance
     */
    public BigDecimal getBalance() {
        return ts.getBalance(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
    }

    /**
     * Given a currency code, return the symbol for that currency
     *
     * @param currency
     * @return currency symbol e.g £
     */
    public String getCurrencySymbol(String currency) {
        switch (currency) {
            case "USD":
                return "$";
            case "GBP":
                return "£";
            case "EUR":
                return "€";
            default:
                return "?";
        }
    }

    /**
     * returns the currency symbol for the current user
     *
     * @return the currency symbol for the current user
     */
    public String getUserCurrencySymbol() {
        return getCurrencySymbol(ts.getUser(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()).getCurrency());
    }

    /**
     * gets transactions received by the current user
     *
     * @return a list of transactions received by the current user
     */
    public List<Notification> getReceivedTransactions() {
        return ts.getReceivedTransactions(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
    }

    /**
     * gets transactions sent by the current user
     *
     * @return a list of transactions sent by the current user
     */
    public List<Notification> getSentTransactions() {
        return ts.getSentTransactions(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
    }

    /**
     * gets all transactions
     *
     * @return a list of all transactions
     */
    @RolesAllowed("Admin")
    public List<Notification> getAllTransactions() {
        return ts.getAllTransactions();
    }

    /**
     * gets notifications received by the current user
     *
     * @return a list of notifications received by the current user
     */
    public List<Notification> getReceivedNotifications() {
        return ts.getReceivedNotifications(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
    }

    /**
     * gets notifications sent by the current user
     *
     * @return a list of notifications sent by the current user
     */
    public List<Notification> getSentNotifications() {
        return ts.getSentNotifications(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
    }

    /**
     * gets number of new notifications
     *
     * @return int containing number of new notifications
     */
    public int getNewNotificationNo() {
        return ts.getNewNotificationNo(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
    }

    /**
     * gets number of new transactions
     *
     * @return int containing number of new transactions
     */
    public int getNewTransactionNo() {
        return ts.getNewTransactionNo(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
    }

}
