package de.skyslycer.hmcwraps.util;

import java.util.List;

public class ListUtil {

    /**
     * Checks if a list contains any element of another list.
     *
     * @param list  The list to check
     * @param other The list to check for
     * @param <T>   The type of the lists
     * @return true If the list contains any element of the other list
     */
    public static <T> boolean containsAny(List<T> list, List<T> other) {
        for (T t : list) {
            if (other.contains(t)) {
                return true;
            }
        }
        return false;
    }

}
