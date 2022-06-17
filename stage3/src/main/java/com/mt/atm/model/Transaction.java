package com.mt.atm.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private String referenceNumber;
    private LocalDateTime timestamp;
    private String accountNumber;

    public Transaction() {
    }

    public Transaction(String referenceNumber, LocalDateTime timestamp, String accountNumber) {
        this.referenceNumber = referenceNumber;
        this.timestamp = timestamp;
        this.accountNumber = accountNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return referenceNumber.equals(that.referenceNumber) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceNumber, timestamp);
    }
}
