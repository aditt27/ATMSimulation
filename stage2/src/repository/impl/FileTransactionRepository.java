package repository.impl;

import model.Transaction;
import model.Transfer;
import model.Withdraw;
import repository.TransactionRepository;
import util.Util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static util.Constant.*;

public class FileTransactionRepository implements TransactionRepository {

    private static final String[] CSV_HEADER = {"Reference Number", "Account Number", "Timestamp", "Transaction Type", "Amount", "Recipient Account Number"};
    private static final String COMMA_DELIMITER = ";";

    private final String fileLocation;
    private List<Transaction> transactions;

    public FileTransactionRepository(String fileLocation) {
        this.fileLocation = fileLocation;
        readTransactionsFromFile();
    }

    public List<Transaction> findAll() {
        return transactions;
    }

    public Transaction save(Transaction transaction) {
        if(transactions.contains(transaction)) {
            int accountIndex = transactions.indexOf(transaction);
            transactions.set(accountIndex, transaction);
        } else {
            transactions.add(transaction);
        }
        writeTransactionsToFile();
        return transaction;
    }

    public List<Transaction> findByAccountNumber(String accountNumber) {
        return transactions.stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .collect(Collectors.toList());
    }

    private void readTransactionsFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileLocation));

            Optional<String> optReadHeader = br.lines().findFirst();
            if(!optReadHeader.isPresent()) {
                throw new FileNotFoundException();
            }

            String[] readHeader = optReadHeader.get().split(COMMA_DELIMITER);
            if(!Arrays.equals(readHeader, CSV_HEADER)) {
                throw new FileNotFoundException();
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
            transactions = br.lines()
                    .map(line-> line.split(COMMA_DELIMITER))
                    .map(rows-> {
                        if(TRANSACTION_TYPE_WITHDRAW.equalsIgnoreCase(rows[3])) {
                            return new Withdraw(
                                    rows[0],
                                    LocalDateTime.parse(rows[2], dtf),
                                    rows[1],
                                    Integer.parseInt(rows[4])
                            );
                        }
                        return new Transfer(
                                rows[0],
                                LocalDateTime.parse(rows[2], dtf),
                                rows[1],
                                rows[5],
                                Integer.parseInt(rows[4])
                        );
                    })
                    .collect(Collectors.toList());
            br.close();

            Set<String> duplicate = Util.checkListContainDuplicate(transactions).stream()
                    .map(Transaction::getReferenceNumber)
                    .collect(Collectors.toSet());

            if(!duplicate.isEmpty()) {
                System.out.println("ATM Error: There can't be 2 different Transaction with the same Reference Number " + duplicate);
                System.exit(0);
            }

        } catch (IOException e) {
            System.out.println("ATM Error: Failed to load Transaction Data");
            System.exit(0);
        }
    }

    private void writeTransactionsToFile() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
            List<String> data = transactions.stream()
                    .map(transaction -> {
                        if(transaction instanceof Withdraw) {
                            Withdraw wd = (Withdraw) transaction;
                            return new String[]{wd.getReferenceNumber(), wd.getAccountNumber(), wd.getTimestamp().format(dtf), TRANSACTION_TYPE_WITHDRAW, Integer.toString(wd.getAmount()), null};
                         }
                        Transfer tf = (Transfer) transaction;
                        return new String[]{tf.getReferenceNumber(), tf.getAccountNumber(), tf.getTimestamp().format(dtf), TRANSACTION_TYPE_TRANSFER, Integer.toString(tf.getAmount()), tf.getRecipientAccountNumber()};
                    })
                    .map(array-> String.join(COMMA_DELIMITER, array))
                    .collect(Collectors.toList());

            BufferedWriter bw = new BufferedWriter(new FileWriter(fileLocation));
            bw.write(String.join(COMMA_DELIMITER, CSV_HEADER));
            bw.newLine();

            for(String item : data) {
                bw.write(item);
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
