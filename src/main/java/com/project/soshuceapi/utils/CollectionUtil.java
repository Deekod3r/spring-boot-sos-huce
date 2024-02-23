package com.project.soshuceapi.utils;

public class CollectionUtil {

    public static <T> boolean isNullOrEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNullOrEmpty(Iterable<T> iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }

    public static <T> boolean isNullOrEmpty(Iterable<T> iterable, int size) {
        return iterable == null || !iterable.iterator().hasNext() || size == 0;
    }

    public static <T> boolean isNullOrEmpty(Iterable<T> iterable, long size) {
        return iterable == null || !iterable.iterator().hasNext() || size == 0;
    }

}
