package repository;

import model.Account;
import util.Util;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AccountRepository {

    private static final String[] CSV_HEADER = {"Name", "PIN", "Balance", "Account Number"};
    private static final String COMMA_DELIMITER = ";";

    public static List<Account> readAccountsFromFile(String fileLocation) {
        List<Account> accounts = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileLocation));

            Optional<String> optReadHeader = br.lines().findFirst();
            if(!optReadHeader.isPresent()) {
                throw new FileNotFoundException();
            }

            String[] readHeader = optReadHeader.get().split(COMMA_DELIMITER);
            if(!Arrays.equals(readHeader, CSV_HEADER)) {
                throw new FileNotFoundException();
            }

            accounts = br.lines()
                    .map(line-> line.split(COMMA_DELIMITER))
                    .map(rows-> new Account(rows[0], rows[1], rows[3], Integer.parseInt(rows[2])))
                    .collect(Collectors.toList());
            br.close();
            Set<String> duplicate = Util.checkListContainDuplicate(accounts).stream()
                    .map(Account::getAccountNumber)
                    .collect(Collectors.toSet());

            if(duplicate.size()>0) {
                System.out.println("ATM Error: There can't be 2 different accounts with the same Account Number " + duplicate.toString());
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("ATM Error: Failed to load Account Data");
            System.exit(0);
        }
        return accounts;
    }

    public static void writeAccountsToFile(String fileLocation, List<Account> accounts) {
        try {
            List<String> data = accounts.stream()
                    .map(account -> new String[]{account.getName(), account.getPin(), Integer.toString(account.getBalance()), account.getAccountNumber()})
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
