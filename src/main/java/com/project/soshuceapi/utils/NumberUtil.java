package com.project.soshuceapi.utils;

import java.util.Objects;

public class NumberUtil {

    public static boolean isNullOrZero(Integer number) {
        return Objects.isNull(number) || number == 0;
    }

    public static boolean isNullOrZero(Long number) {
        return Objects.isNull(number) || number == 0L;
    }

    public static boolean isNullOrZero(Double number) {
        return Objects.isNull(number) || number == 0D;
    }

    public static boolean isNullOrZero(Float number) {
        return Objects.isNull(number) || number == 0F;
    }

    public static boolean isNullOrZero(String number) {
        return Objects.isNull(number) || number.isEmpty() || number.equals("0");
    }

    public static boolean isNullOrZero(Object number) {
        return Objects.isNull(number) || number.toString().isEmpty() || number.toString().equals("0");
    }

    public static boolean isNullOrZero(Object[] number) {
        return Objects.isNull(number) || number.length == 0;
    }

    public static boolean isNullOrZero(Iterable<?> number) {
        return Objects.isNull(number) || !number.iterator().hasNext();
    }

}
