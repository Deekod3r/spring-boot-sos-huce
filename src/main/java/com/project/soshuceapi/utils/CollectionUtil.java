package com.project.soshuceapi.utils;

import java.util.Objects;

public class CollectionUtil {

    public static <T> boolean isNullOrEmpty(T[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    public static <T> boolean isNullOrEmpty(Iterable<T> iterable) {
        return Objects.isNull(iterable) || !iterable.iterator().hasNext();
    }

}
