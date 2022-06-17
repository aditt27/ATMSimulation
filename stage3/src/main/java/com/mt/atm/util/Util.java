package util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {

    public static <T> Set<T> checkListContainDuplicate(List<T> list) {
        Set<T> items = new HashSet<>();
        return list.stream()
                .filter(item-> !items.add(item))
                .collect(Collectors.toSet());
    }
}
