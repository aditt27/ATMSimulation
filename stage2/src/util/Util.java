package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static util.Constant.*;

public class Util {

    private Util() {
        throw new IllegalStateException("Utility Class");
    }

    private static final Random RANDOM = new Random();
    private static final Scanner SCANNER = new Scanner(System.in);

    public static <T> Set<T> checkListContainDuplicate(List<T> list) {
        Set<T> items = new HashSet<>();
        return list.stream()
                .filter(item-> !items.add(item))
                .collect(Collectors.toSet());
    }

    public static String randomStringNumber(int length) {
        StringBuilder bound = new StringBuilder();
        for(int i=0; i<length; i++) {
            bound.append("9");
        }
        return String.format("%06d", RANDOM.nextInt(Integer.parseInt(bound.toString())));
    }

    public static String formatStringLocalDateTime(LocalDateTime input) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
        return input.format(dtf);
    }

    public static String displayLineScreen(String screen) {
        System.out.println();
        System.out.print(screen);
        return SCANNER.nextLine();
    }

    public static boolean isNumber(String input) {
        if(input.matches(REGEX_NUMBER_ONLY)) {
            return true;
        }
        return false;
    }

}
