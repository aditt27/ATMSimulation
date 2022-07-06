package repository;

import model.Transaction;

import java.util.List;

public interface TransactionRepository {

    List<Transaction> findAll();

    Transaction save(Transaction transaction);

    List<Transaction> findByAccountNumber(String accountNumber);
}
