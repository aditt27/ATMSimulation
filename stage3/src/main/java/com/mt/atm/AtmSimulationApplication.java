package com.mt.atm;

import com.mt.atm.model.Account;
import com.mt.atm.model.Transaction;
import com.mt.atm.model.TransactionResult;
import com.mt.atm.repository.AccountRepository;
import com.mt.atm.repository.TransactionRepository;
import com.mt.atm.service.MainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class AtmSimulationApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(AtmSimulationApplication.class, args);
//	}

	public static void main(String[] args) {
		List<Account> accounts = AccountRepository.readAccountsFromFile(args[0]);
		List<Transaction> transactions = TransactionRepository.readTransactionsFromFile(args[1]);

		Scanner scanner = new Scanner(System.in);

		Account currentAccount = null;
		while(true) {
			currentAccount = MainService.authenticate(scanner, accounts, currentAccount);

			int menu;
			System.out.print(
					"\n" +
							"1. Withdraw\n" +
							"2. Fund Transfer\n" +
							"3. Transaction History\n" +
							"4. Exit\n" +
							"Please choose option[3]: "
			);
			String sMenu = scanner.nextLine();
			if(!sMenu.matches("\\d+")) {
				continue;
			} else if("".equals(sMenu)) {
				menu = 4;
			} else {
				menu = Integer.parseInt(sMenu);
			}

			switch (menu) {
				case 1:
					TransactionResult result = MainService.withdraw(scanner, currentAccount);

					currentAccount = result.getAccount();
					transactions.add(result.getTransaction());

					AccountRepository.writeAccountsToFile(args[0], accounts);
					TransactionRepository.writeTransactionsToFile(args[1], transactions);
					break;
				case 2:
					result = MainService.transfer(scanner, accounts, currentAccount);

					currentAccount = result.getAccount();
					transactions.add(result.getTransaction());

					AccountRepository.writeAccountsToFile(args[0], accounts);
					TransactionRepository.writeTransactionsToFile(args[1], transactions);
					break;
				case 3:
					MainService.printTransactionHistory(currentAccount, transactions);
					break;
				default:
					currentAccount = null;
					break;
			}
		}
	}

}
