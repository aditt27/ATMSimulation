import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    static List<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {
        accounts.add(new Account("Aditya Budi", "123456", "123678", 1000000));
        accounts.add(new Account("Jon Do", "987654", "923874", 542000));
        accounts.add(new Account("Sam Sul", "563721", "845739", 245000));

        Scanner scanner = new Scanner(System.in);

        Account currentAccount = null;
        while(true) {
            currentAccount = authenticate(scanner, accounts, currentAccount);

            int menu;
            System.out.println();
            System.out.print(
                "1. Withdraw\n" +
                "2. Fund Transfer\n" +
                "3. Exit\n" +
                "Please choose option[3]: "
            );
            String sMenu = scanner.nextLine();
            if(!sMenu.matches("\\d+")) {
                continue;
            } else if("".equals(sMenu)) {
                menu = 3;
            } else {
                menu = Integer.parseInt(sMenu);
            }

            switch (menu) {
                case 1:
                    currentAccount = withdraw(scanner, currentAccount);
                    break;
                case 2:
                    currentAccount = transfer(scanner, accounts, currentAccount);
                    break;
                case 4:
                    System.out.println(accounts.toString());
                    break;
                default:
                    currentAccount = null;
                    break;
            }
        }
    }

    private static Account authenticate(Scanner scanner, List<Account> accounts, Account currentAccount) {
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

    private static Account withdraw(Scanner scanner, Account currentAccount) {
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

        if(withdrawAmount>0) {
            if(withdrawAmount>currentAccount.getBalance()) {
                System.out.println("Insufficient balance $" + withdrawAmount);
                return currentAccount;
            }

            currentAccount.setBalance(currentAccount.getBalance()-withdrawAmount);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
            System.out.println(
                    "Summary\n" +
                            "Date : " + LocalDateTime.now().format(dtf)+ "\n" +
                            "Withdraw : $" + withdrawAmount + "\n" +
                            "Balance : $"+ currentAccount.getBalance() + "\n"
            );
        }
        return currentAccount;
    }

    private static Account transfer(Scanner scanner, List<Account> accounts, Account currentAccount) {
        System.out.print("Destination Account Number: ");
        String accNum = scanner.nextLine();

        if(!accNum.matches("\\d+")) {
            System.out.println("Invalid Account");
            return currentAccount;
        }

        Account destinationAccount = null;
        for(Account account : accounts) {
            if(account.getAccountNumber().equals(accNum)){
                destinationAccount = account;
            }
        }

        if(destinationAccount==null) {
            System.out.println("Invalid Account");
            return currentAccount;
        }

        int transferAmount = 0;
        try {
            System.out.println();
            System.out.print("Transfer Amount: ");

            int amount = scanner.nextInt();
            scanner.nextLine();
            if(amount>1000) {
                System.out.println("Maximum amount to withdraw is $1000");
                return currentAccount;
            }
            if(amount<=0) {
                System.out.println("Minimum amount to transfer is $1");
                return currentAccount;
            }
            transferAmount = amount;
        } catch (InputMismatchException e) {
            System.out.println("Invalid Amount");
            scanner.nextLine();
        }

        if(transferAmount>0) {
            if(transferAmount>currentAccount.getBalance()) {
                System.out.println("Insufficient balance $" + transferAmount);
                return currentAccount;
            }

            Random rand = new Random();
            String referenceNum = String.format("%06d", rand.nextInt(999999));

            int option = 0;
            System.out.println();
            System.out.print(
                    "Transfer Confirmation\n" +
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
                return currentAccount;
            }  else {
                option = Integer.parseInt(sOption);
            }

            if(option==1) {
                currentAccount.setBalance(currentAccount.getBalance()-transferAmount);
                destinationAccount.setBalance(destinationAccount.getBalance()+transferAmount);

                System.out.println(
                        "Fund Transfer Summary\n" +
                                "Destination Account : " + destinationAccount.getAccountNumber() + "\n" +
                                "Transfer Amount     : $" + transferAmount + "\n" +
                                "Reference Number    : " + referenceNum + "\n" +
                                "Balance             : $" + currentAccount.getBalance()
                );
            }
        }
        return currentAccount;
    }


}
