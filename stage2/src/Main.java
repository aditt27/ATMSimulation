import model.*;
import service.*;
import util.AtmValidator;
import util.Util;

public class Main {

    public static void main(String[] args) {
        AtmService atmService = new AtmService(args);

        Account currentAccount = null;
        while(true) {
            currentAccount = atmService.authenticate(currentAccount);

            String sMenu = Util.displayLineScreen(
                "1. Withdraw\n" +
                "2. Fund Transfer\n" +
                "3. Transaction History\n" +
                "4. Exit\n" +
                "Please choose option[3]: "
            );

            int menu;
            if(!Util.isNumber(sMenu)) {
                continue;
            } else if("".equals(sMenu)) {
                menu = 4;
            } else {
                menu = Integer.parseInt(sMenu);
            }

            switch (menu) {
                case 1:
                    currentAccount = atmService.withdraw(currentAccount);
                    break;
                case 2:
                    currentAccount = atmService.transfer(currentAccount);
                    break;
                case 3:
                    atmService.printTransactionHistory(currentAccount);
                    break;
                default:
                    currentAccount = null;
                    break;
            }
        }
    }
}
