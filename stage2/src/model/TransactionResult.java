package model;

import model.Account;
import model.Transaction;
import model.Transfer;

public class TransactionResult {
    private Account account;
    private Transaction transaction;

    public TransactionResult(Account account, Transaction transaction) {
        this.account = account;
        this.transaction = transaction;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
