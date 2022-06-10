package repository;

import model.Transaction;
import model.Transfer;
import model.Withdraw;
import util.Util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionRepository {

    private static final String[] CSV_HEADER = {"Reference Number", "Account Number", "Timestamp", "Transaction Type", "Amount", "Recipient Account Number"};
    private static final String COMMA_DELIMITER = ";";


    public static List<Transaction> readTransactionsFromFile(String fileLocation) {
        List<Transaction> transactions = new ArrayList<>();

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

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
            transactions = br.lines()
                    .map(line-> line.split(COMMA_DELIMITER))
                    .map(rows-> {
                        if("withdraw".equalsIgnoreCase(rows[3])) {
                            return new Withdraw(rows[0], LocalDateTime.parse(rows[2], dtf), rows[1], Integer.parseInt(rows[4]));
                        }
                        return new Transfer(rows[0], LocalDateTime.parse(rows[2], dtf), rows[1], rows[5], Integer.parseInt(rows[4]));
                    })
                    .collect(Collectors.toList());
            br.close();

            Set<String> duplicate = Util.checkListContainDuplicate(transactions).stream()
                    .map(Transaction::getReferenceNumber)
                    .collect(Collectors.toSet());

            if(duplicate.size()>0) {
                System.out.println("ATM Error: There can't be 2 different Transaction with the same Reference Number " + duplicate.toString());
                System.exit(0);
            }

        } catch (IOException e) {
            System.out.println("ATM Error: Failed to load Transaction Data");
            System.exit(0);
        }
        return transactions;
    }

    public static void writeTransactionsToFile(String fileLocation, List<Transaction> transactions) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
            List<String> data = transactions.stream()
                    .map(transaction -> {
                        if(transaction instanceof Withdraw) {
                            Withdraw wd = (Withdraw) transaction;
                            return new String[]{wd.getReferenceNumber(), wd.getAccountNumber(), wd.getTimestamp().format(dtf), "Withdraw", Integer.toString(wd.getAmount()), null};
                         }
                        Transfer tf = (Transfer) transaction;
                        return new String[]{tf.getReferenceNumber(), tf.getAccountNumber(), tf.getTimestamp().format(dtf), "Transfer", Integer.toString(tf.getAmount()), tf.getRecipientAccountNumber()};
                    })
                    .map(array-> String.join(";", array))
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
