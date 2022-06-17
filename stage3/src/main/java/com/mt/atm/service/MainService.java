package com.mt.atm.service;


import com.mt.atm.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MainService {

    public static Account authenticate(Scanner scanner, List<Account> accounts, Account currentAccount) {
        while(currentAccount==null) {
            System.out.println();
            System.out.print("Enter Account Number: ");
            String accNum = scanner.nextLine();

            if(accNum.length()!=6) {
                System.out.println("Account Number should have 6 digits length");
                continue;
            }

            if(!accNum.matches("\\d+")) {
                System.out.println("Account Number should only contains numbers");
                continue;
            }

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine();

            if(pin.length()!=6) {
                System.out.println("PIN should have 6 digits length");
                continue;
            }

            if(!pin.matches("\\d+")) {
                System.out.println("PIN should only contains numbers");
                continue;
            }

            for(Account acc : accounts) {
                if(acc.getAccountNumber().equals(accNum)) {
                    if(acc.getPin().equals(pin)) {
                        currentAccount = acc;
                    }
                    break;
                }
            }

            if(currentAccount==null) {
                System.out.println("Invalid Account Number/PIN");
            }
        }
        return currentAccount;
    }

    public static TransactionResult withdraw(Scanner scanner, Account currentAccount) {
        int option;
        System.out.println();
        System.out.print(
                "1. $10\n" +
                        "2. $50\n" +
                        "3. $100\n" +
                        "4. Other\n" +
                        "5. Back\n" +
                        "Please choose option[5]: "
        );
        String sOption = scanner.nextLine();
        if(!sOption.matches("\\d+") || "".equals(sOption)) {
            option = 5;
        } else {
            option = Integer.parseInt(sOption);
        }

        int withdrawAmount = 0;
        switch (option) {
            case 1:
                withdrawAmount = 10;
                break;
            case 2:
                withdrawAmount = 50;
                break;
            case 3:
                withdrawAmount = 100;
                break;
            case 4:
                try {
                    System.out.println();
                    System.out.print(
                            "Other Withdraw\n" +
                                    "Enter amount to withdraw: "
                    );

                    int amount = scanner.nextInt();
                    scanner.nextLine();
                    if(amount>1000) {
                        System.out.println("Maximum amount to withdraw is $1000");
                        break;
                    }
                    if(amount%10!=0) {
                        System.out.println("Invalid Amount");
                        break;
                    }
                    withdrawAmount = amount;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid Amount");
                    scanner.nextLine();
                }
                break;
            default:
                break;
        }

        Withdraw withdraw = null;
        if(withdrawAmount>0) {
            if(withdrawAmount>currentAccount.getBalance()) {
                System.out.println("Insufficient balance $" + withdrawAmount);
                return new TransactionResult(currentAccount, null);
            }

            currentAccount.setBalance(currentAccount.getBalance()-withdrawAmount);

            Random rand = new Random();
            String referenceNum = String.format("%06d", rand.nextInt(999999));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
            LocalDateTime dateTime = LocalDateTime.now();

            withdraw = new Withdraw(referenceNum, dateTime, currentAccount.getAccountNumber(), withdrawAmount);

            System.out.println(
                    "Withdraw Summary\n" +
                            "Date              : " + dateTime.format(dtf) + "\n" +
                            "Reference Number  : " + referenceNum + "\n" +
                            "Withdraw          : $" + withdrawAmount + "\n" +
                            "Balance           : $"+ currentAccount.getBalance() + "\n"
            );
        }
        return new TransactionResult(currentAccount, withdraw);
    }

    public static TransactionResult transfer(Scanner scanner, List<Account> accounts, Account currentAccount) {
        System.out.print("Destination Account Number: ");
        String accNum = scanner.nextLine();

        if(!accNum.matches("\\d+")) {
            System.out.println("Invalid Account");
            return new TransactionResult(currentAccount, null);
        }

        Account destinationAccount = null;
        for(Account account : accounts) {
            if(account.getAccountNumber().equals(accNum)){
                destinationAccount = account;
            }
        }

        if(destinationAccount==null) {
            System.out.println("Invalid Account");
            return new TransactionResult(currentAccount, null);
        }

        int transferAmount = 0;
        try {
            System.out.println();
            System.out.print("Transfer Amount: ");

            int amount = scanner.nextInt();
            scanner.nextLine();
            if(amount>1000) {
                System.out.println("Maximum amount to withdraw is $1000");
                return new TransactionResult(currentAccount, null);
            }
            if(amount<=0) {
                System.out.println("Minimum amount to transfer is $1");
                return new TransactionResult(currentAccount, null);
            }
            transferAmount = amount;
        } catch (InputMismatchException e) {
            System.out.println("Invalid Amount");
            scanner.nextLine();
        }

        Transfer transfer = null;
        if(transferAmount>0) {
            if(transferAmount>currentAccount.getBalance()) {
                System.out.println("Insufficient balance $" + transferAmount);
                return new TransactionResult(currentAccount, null);
            }

            Random rand = new Random();
            String referenceNum = String.format("%06d", rand.nextInt(999999));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
            LocalDateTime dateTime = LocalDateTime.now();

            int option;
            System.out.println();
            System.out.print(
                    "Transfer Confirmation\n" +
                            "Date                : " + dateTime.format(dtf) + "\n" +
                            "Destination Account : " + destinationAccount.getAccountNumber() + "\n" +
                            "Transfer Amount     : $" + transferAmount + "\n" +
                            "Reference Number    : " + referenceNum + "\n" +
                            "\n" +
                            "1. Confirm Trx\n" +
                            "2. Cancel Trx\n" +
                            "Choose option[2]:"
            );
            String sOption = scanner.nextLine();
            if(!sOption.matches("\\d+") || "".equals(sOption)) {
                return new TransactionResult(currentAccount, null);
            }  else {
                option = Integer.parseInt(sOption);
            }

            if(option==1) {
                currentAccount.setBalance(currentAccount.getBalance()-transferAmount);
                destinationAccount.setBalance(destinationAccount.getBalance()+transferAmount);

                transfer = new Transfer(referenceNum, dateTime, currentAccount.getAccountNumber(), destinationAccount.getAccountNumber(), transferAmount);

                System.out.println(
                        "Fund Transfer Summary\n" +
                                "Date                : " + dateTime.format(dtf) + "\n" +
                                "Destination Account : " + destinationAccount.getAccountNumber() + "\n" +
                                "Transfer Amount     : $" + transferAmount + "\n" +
                                "Reference Number    : " + referenceNum + "\n" +
                                "Balance             : $" + currentAccount.getBalance()
                );
            }
        }
        return new TransactionResult(currentAccount, transfer);
    }

    public static void printTransactionHistory(Account currentAccount, List<Transaction> transactions) {
        List<Transaction> accTransaction = transactions.stream()
                .filter(transaction -> transaction.getAccountNumber().equals(currentAccount.getAccountNumber()))
                .collect(Collectors.toList());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
        System.out.println("Reference Number|Account Number|Timestamp|Transaction Type|Amount|Recipient Account Number");
        for(Transaction tr : accTransaction) {
            if(tr instanceof Withdraw) {
                Withdraw wd = (Withdraw) tr;
                System.out.println(wd.getReferenceNumber()+"|"+wd.getAccountNumber()+"|"+wd.getTimestamp().format(dtf)+"|"+"Withdraw"+"|"+"$"+wd.getAmount()+"|"+"-");
                continue;
            }
            Transfer tf = (Transfer) tr;
            System.out.println(tf.getReferenceNumber()+"|"+tf.getAccountNumber()+"|"+tf.getTimestamp().format(dtf)+"|"+"Withdraw"+"|"+"$"+tf.getAmount()+"|"+tf.getRecipientAccountNumber());
        }
    }
}
