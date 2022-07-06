package repository;

import model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    List<Account> findAll();

    Optional<Account> findByAccountNumber(String accountNumber);

    Account save(Account account);
}
