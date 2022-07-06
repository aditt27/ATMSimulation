package service;

import model.*;
import repository.AccountRepository;
import repository.TransactionRepository;
import repository.impl.FileAccountRepository;
import repository.impl.FileTransactionRepository;
import util.AtmValidator;
import util.Util;

import java.time.LocalDateTime;
import java.util.*;

import static util.Constant.*;


public class AtmService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AtmService(String[] args) {
        accountRepository = new FileAccountRepository(args[0]);
        transactionRepository = new FileTransactionRepository(args[1]);
    }

    public Account authenticate(Account currentAccount) {
        while(currentAccount==null) {
            String accNum;
            String pin;
            try {
                accNum = Util.displayLineScreen("Enter Account Number: ");
                AtmValidator.isAccountNumber(accNum);

                pin = Util.displayLineScreen("Enter PIN: ");
                AtmValidator.isPin(pin);

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            Optional<Account> accountOptional = accountRepository.findByAccountNumber(accNum);
            if(accountOptional.isPresent() && accountOptional.get().getPin().equals(pin)) {
                currentAccount = accountOptional.get();
                break;
            } else {
                System.out.println("Invalid Account Number/PIN");
            }
        }
        return currentAccount;
    }

    public Account withdraw(Account currentAccount) {
        String sOption = Util.displayLineScreen(
                "1. $10\n" +
                "2. $50\n" +
                "3. $100\n" +
                "4. Other\n" +
                "5. Back\n" +
                "Please choose option[5]: "
        );
        int option = (!Util.isNumber(sOption) || sOption.isEmpty())? 5 : Integer.parseInt(sOption);

        int withdrawAmount = 0;
        switch (option) {
            case 1: withdrawAmount = 10; break;
            case 2: withdrawAmount = 50; break;
            case 3: withdrawAmount = 100; break;
            case 4:
                try {
                    withdrawAmount = Integer.parseInt(Util.displayLineScreen(
                            "Other Withdraw\n" +
                            "Enter amount to withdraw: "
                    ));
                    AtmValidator.isValidWithdrawAmount(withdrawAmount);
                } catch (InputMismatchException e) {
                    System.out.println("Invalid Amount");
                    return currentAccount;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    return currentAccount;
                }
                break;
            default: break;
        }

        if(withdrawAmount>0) {
            if(withdrawAmount>currentAccount.getBalance()) {
                System.out.println("Insufficient balance $" + withdrawAmount);
                return currentAccount;
            }

            currentAccount.setBalance(currentAccount.getBalance()-withdrawAmount);

            String referenceNum = Util.randomStringNumber(6);

            LocalDateTime currentDateTime = LocalDateTime.now();
            String dateTime = Util.formatStringLocalDateTime(currentDateTime);

            Withdraw withdraw = new Withdraw(referenceNum, currentDateTime, currentAccount.getAccountNumber(), withdrawAmount);
            transactionRepository.save(withdraw);

            System.out.println(
                    "Withdraw Summary\n" +
                            "Date              : " + dateTime + "\n" +
                            "Reference Number  : " + referenceNum + "\n" +
                            "Withdraw          : $" + withdrawAmount + "\n" +
                            "Balance           : $"+ currentAccount.getBalance() + "\n"
            );
        }
        return currentAccount;
    }

    public Account transfer(Account currentAccount) {

        String accNum = Util.displayLineScreen("Destination Account Number: ");

        if(!AtmValidator.isAccountNumber(accNum)) {
            System.out.println("Invalid Account");
            return currentAccount;
        }

        Optional<Account> destinationAccount = accountRepository.findByAccountNumber(accNum);
        if(!destinationAccount.isPresent()) {
            System.out.println("Invalid Account");
            return currentAccount;
        }

        int transferAmount = 0;
        try {
            transferAmount = Integer.parseInt(Util.displayLineScreen("Transfer Amount: "));
            AtmValidator.isValidTransferAmount(transferAmount);
        } catch (InputMismatchException e) {
            System.out.println("Invalid Amount");
            return currentAccount;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return currentAccount;
        }

        if(transferAmount>0) {
            if(transferAmount>currentAccount.getBalance()) {
                System.out.println("Insufficient balance $" + transferAmount);
                return currentAccount;
            }

            String referenceNum = Util.randomStringNumber(6);
            LocalDateTime currentDateTime = LocalDateTime.now();
            String dateTime = Util.formatStringLocalDateTime(currentDateTime);

            String sOption = Util.displayLineScreen(
                    "Transfer Confirmation\n" +
                            "Date                : " + dateTime + "\n" +
                            "Destination Account : " + destinationAccount.get().getAccountNumber() + "\n" +
                            "Transfer Amount     : $" + transferAmount + "\n" +
                            "Reference Number    : " + referenceNum + "\n" +
                            "\n" +
                            "1. Confirm Trx\n" +
                            "2. Cancel Trx\n" +
                            "Choose option[2]:"
            );

            if(!Util.isNumber(sOption) || sOption.isEmpty()) {
                return currentAccount;
            }

            if(sOption.equals("1")) {
                currentAccount.setBalance(currentAccount.getBalance()-transferAmount);
                destinationAccount.get().setBalance(destinationAccount.get().getBalance()+transferAmount);

                Transfer transfer = new Transfer(referenceNum, currentDateTime, currentAccount.getAccountNumber(), destinationAccount.get().getAccountNumber(), transferAmount);
                transactionRepository.save(transfer);

                System.out.println(
                        "Fund Transfer Summary\n" +
                                "Date                : " + dateTime + "\n" +
                                "Destination Account : " + destinationAccount.get().getAccountNumber() + "\n" +
                                "Transfer Amount     : $" + transferAmount + "\n" +
                                "Reference Number    : " + referenceNum + "\n" +
                                "Balance             : $" + currentAccount.getBalance()
                );
            }
        }
        return currentAccount;
    }

    public void printTransactionHistory(Account currentAccount) {
        List<Transaction> accTransaction = transactionRepository.findByAccountNumber(currentAccount.getAccountNumber());

        System.out.println("Reference Number|Account Number|Timestamp|Transaction Type|Amount|Recipient Account Number");
        for(Transaction tr : accTransaction) {
            if(tr instanceof Withdraw) {
                Withdraw wd = (Withdraw) tr;
                System.out.println(wd.getReferenceNumber()+"|"+wd.getAccountNumber()+"|"+Util.formatStringLocalDateTime(wd.getTimestamp())+"|"+TRANSACTION_TYPE_WITHDRAW+"|"+"$"+wd.getAmount()+"|"+"-");
                continue;
            }
            Transfer tf = (Transfer) tr;
            System.out.println(tf.getReferenceNumber()+"|"+tf.getAccountNumber()+"|"+Util.formatStringLocalDateTime(tf.getTimestamp())+"|"+TRANSACTION_TYPE_TRANSFER+"|"+"$"+tf.getAmount()+"|"+tf.getRecipientAccountNumber());
        }
    }
}
