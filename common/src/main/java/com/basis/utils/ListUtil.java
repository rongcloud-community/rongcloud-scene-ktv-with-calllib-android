package com.basis.utils;

import java.util.List;

public class ListUtil {
    public ListUtil() {
    }

    public static boolean isNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    public static boolean isEmpty(List list) {
        return !isNotEmpty(list);
    }
}