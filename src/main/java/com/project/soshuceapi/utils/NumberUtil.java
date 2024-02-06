package com.project.soshuceapi.utils;

public class NumberUtil {

    public static boolean isNullOrZero(Integer number) {
        return number == null || number == 0;
    }

    public static boolean isNullOrZero(Long number) {
        return number == null || number == 0;
    }

    public static boolean isNullOrZero(Double number) {
        return number == null || number == 0;
    }

    public static boolean isNullOrZero(Float number) {
        return number == null || number == 0;
    }

    public static boolean isNullOrZero(Short number) {
        return number == null || number == 0;
    }

    public static boolean isNullOrZero(Byte number) {
        return number == null || number == 0;
    }

    public static boolean isNullOrZero(Number number) {
        return number == null || number.doubleValue() == 0;
    }

    public static boolean isNullOrZero(String number) {
        return number == null || number.isEmpty() || number.equals("0");
    }

    public static boolean isNullOrZero(Object number) {
        return number == null || number.toString().isEmpty() || number.toString().equals("0");
    }

    public static boolean isNullOrZero(Object[] number) {
        return number == null || number.length == 0;
    }

    public static boolean isNullOrZero(Iterable<?> number) {
        return number == null || !number.iterator().hasNext();
    }
}
