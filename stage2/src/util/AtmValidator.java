package util;

import static util.Constant.*;

public class AtmValidator {

    private AtmValidator() {
        throw new IllegalStateException("Utility Class");
    }

    public static boolean isAccountNumber(String input) {
        if(input.length()!= ACCOUNT_NUMBER_LENGTH) {
            throw new IllegalArgumentException("Account Number should have " + ACCOUNT_NUMBER_LENGTH + " digits length");
        }

        if(!Util.isNumber(input)) {
            throw new IllegalArgumentException("Account Number should only contains numbers");
        }
        return true;
    }

    public static boolean isPin(String input) {
        if(input.length()!=PIN_LENGTH) {
            throw new IllegalArgumentException("PIN should have " + PIN_LENGTH + " digits length");
        }

        if(!Util.isNumber(input)) {
            throw new IllegalArgumentException("PIN should only contains numbers");
        }
        return true;
    }

    public static boolean isValidWithdrawAmount(int amount) {
        if(amount>MAX_WITHDRAW_AMOUNT) {
            throw new IllegalArgumentException("Maximum amount to withdraw is $" + MAX_WITHDRAW_AMOUNT);

        }
        if(amount%CASH_NOTATION!=0) {
            throw new IllegalArgumentException("Invalid Amount");
        }
        return true;
    }

    public static boolean isValidTransferAmount(int amount) {
        if(amount>MAX_TRANSFER_AMOUNT) {
            throw new IllegalArgumentException("Maximum amount to withdraw is $" + MAX_TRANSFER_AMOUNT);
        }
        if(amount<MIN_TRANSFER_AMOUNT) {
            throw new IllegalArgumentException("Minimum amount to transfer is $" + MIN_TRANSFER_AMOUNT);
        }
        return true;
    }

}
