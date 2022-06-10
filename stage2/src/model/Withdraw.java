package model;

import java.time.LocalDateTime;

public class Withdraw extends Transaction{

    private int amount;

    public Withdraw(String referenceNumber, LocalDateTime timestamp, String accountNumber, int amount) {
        super(referenceNumber, timestamp, accountNumber);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
