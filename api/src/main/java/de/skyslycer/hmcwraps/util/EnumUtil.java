package de.skyslycer.hmcwraps.util;

import java.util.ArrayList;
import java.util.List;

public class EnumUtil {

    public static <T extends Enum<T>> List<T> getAllPossibilities(List<String> raw, Class<T> clazz) {
        List<T> newList = new ArrayList<>();
        raw.forEach(it -> {
            try {
                newList.add(T.valueOf(clazz, it));
            } catch (IllegalArgumentException ignored) {
            }
        });
        return newList;
    }

}
