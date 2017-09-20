/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ad436.webappsCW.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 * Database model for transactions and notifications
 *
 * @author ad436
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "getSentNotifications", query = "SELECT c FROM Notification c WHERE c.sender.username = :username AND c.request = true"),
    @NamedQuery(name = "getReceivedNotifications", query = "SELECT c FROM Notification c WHERE c.recipient.username = :username AND c.request = true"),
    @NamedQuery(name = "getSentTransactionsForUser", query = "SELECT c FROM Notification c WHERE c.sender.username = :username AND c.request = false"),
    @NamedQuery(name = "getAllTransactions", query = "SELECT c FROM Notification c"),
    @NamedQuery(name = "getReceivedTransactionsForUser", query = "SELECT c FROM Notification c WHERE c.recipient.username = :username AND c.request = false"),})

public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @OneToOne
    SystemUser recipient;
    @NotNull
    @OneToOne
    SystemUser sender;
    @NotNull
    String currency;
    @NotNull
    BigDecimal amount;
    @NotNull
    BigDecimal senderNewBalance, receiverNewBalance;
    @NotNull
    @Temporal(javax.persistence.TemporalType.DATE)
    Date transactionDate;

    boolean seen, request, rejected; //request is true if notification, false if transaction

    public Notification() {
    }

    /**
     * Creates a new notification
     *
     * @param sender
     * @param recipient
     * @param currency
     * @param amount
     * @param transactionDate
     * @param seen
     * @param request
     * @param rejected
     */
    public Notification(SystemUser sender, SystemUser recipient, String currency, BigDecimal amount, Date transactionDate, boolean seen, boolean request, boolean rejected) {
        this.recipient = recipient;
        this.sender = sender;
        this.currency = currency;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.transactionDate = transactionDate;
        this.seen = seen;
        this.request = request;
        this.rejected = rejected;

    }

    /**
     * Creates a new transaction keeping track of the sender/receivers balance
     *
     * @param sender
     * @param recipient
     * @param currency
     * @param amount
     * @param senderNewBalance
     * @param receiverNewBalance
     * @param transactionDate
     * @param seen
     * @param request
     * @param rejected
     */
    public Notification(SystemUser sender, SystemUser recipient, String currency, BigDecimal amount, BigDecimal senderNewBalance, BigDecimal receiverNewBalance, Date transactionDate, boolean seen, boolean request, boolean rejected) {
        this.recipient = recipient;
        this.sender = sender;
        this.currency = currency;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.transactionDate = transactionDate;
        this.seen = seen;
        this.request = request;
        this.rejected = rejected;
        this.senderNewBalance = senderNewBalance;
        this.receiverNewBalance = receiverNewBalance;

    }

    public BigDecimal getSenderNewBalance() {
        return senderNewBalance;
    }

    public BigDecimal getReceiverNewBalance() {
        return receiverNewBalance;
    }

    public void setReceiverNewBalance(BigDecimal receiverNewBalance) {
        this.receiverNewBalance = receiverNewBalance;
    }

    public void setSenderNewBalance(BigDecimal senderNewBalance) {
        this.senderNewBalance = senderNewBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isRejected() {
        return rejected;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.recipient);
        hash = 89 * hash + Objects.hashCode(this.sender);
        hash = 89 * hash + Objects.hashCode(this.currency);
        hash = 89 * hash + Objects.hashCode(this.amount);
        hash = 89 * hash + Objects.hashCode(this.senderNewBalance);
        hash = 89 * hash + Objects.hashCode(this.receiverNewBalance);
        hash = 89 * hash + Objects.hashCode(this.transactionDate);
        hash = 89 * hash + (this.seen ? 1 : 0);
        hash = 89 * hash + (this.request ? 1 : 0);
        hash = 89 * hash + (this.rejected ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (this.seen != other.seen) {
            return false;
        }
        if (this.request != other.request) {
            return false;
        }
        if (this.rejected != other.rejected) {
            return false;
        }
        if (!Objects.equals(this.currency, other.currency)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.recipient, other.recipient)) {
            return false;
        }
        if (!Objects.equals(this.sender, other.sender)) {
            return false;
        }
        if (!Objects.equals(this.amount, other.amount)) {
            return false;
        }
        if (!Objects.equals(this.senderNewBalance, other.senderNewBalance)) {
            return false;
        }
        if (!Objects.equals(this.receiverNewBalance, other.receiverNewBalance)) {
            return false;
        }
        if (!Objects.equals(this.transactionDate, other.transactionDate)) {
            return false;
        }
        return true;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public boolean isRequest() {
        return request;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }

    public SystemUser getRecipient() {
        return recipient;
    }

    public void setRecipient(SystemUser recipient) {
        this.recipient = recipient;
    }

    public SystemUser getSender() {
        return sender;
    }

    public void setSender(SystemUser sender) {
        this.sender = sender;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Long getId() {
        return id;
    }

}
