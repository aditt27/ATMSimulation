package com.mt.atm.model;

import java.time.LocalDateTime;

public class Transfer extends Transaction {

    private String recipientAccountNumber;
    private int amount;

    public Transfer() {
    }

    public Transfer(String referenceNumber, LocalDateTime timestamp, String accountNumber, String recipientAccountNumber, int amount) {
        super(referenceNumber, timestamp, accountNumber);
        this.recipientAccountNumber = recipientAccountNumber;
        this.amount = amount;
    }

    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }

    public void setRecipientAccountNumber(String recipientAccountNumber) {
        this.recipientAccountNumber = recipientAccountNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
