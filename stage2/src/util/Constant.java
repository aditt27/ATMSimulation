package util;

public class Constant {

    private Constant() {
        throw new IllegalStateException("Utility Class");
    }

    public static final int PIN_LENGTH = 6;
    public static final int ACCOUNT_NUMBER_LENGTH = 6;
    public static final int MAX_WITHDRAW_AMOUNT = 1000;
    public static final int MAX_TRANSFER_AMOUNT = 1000;
    public static final int MIN_TRANSFER_AMOUNT = 1;
    public static final int CASH_NOTATION = 10;

    public static final String TRANSACTION_TYPE_WITHDRAW = "Withdraw";
    public static final String TRANSACTION_TYPE_TRANSFER = "Transfer";

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm a";
    public static final String REGEX_NUMBER_ONLY = "\\d+";


}
