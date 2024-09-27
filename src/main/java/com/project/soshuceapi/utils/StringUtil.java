package com.project.soshuceapi.utils;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class StringUtil {

    private StringUtil() {
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }

    public static boolean isNullOrEmpty(String str) {
        return Objects.isNull(str) || str.isEmpty();
    }

    public static boolean isNullOrBlank(String str) {
        return Objects.isNull(str) || str.isBlank();
    }

    public static String uppercaseFirstLetter(String input) {
        if (isNullOrBlank(input)) {
            return input;
        }

        char firstChar = Character.toUpperCase(input.charAt(0));
        return firstChar + input.substring(1);
    }

    public static String uppercaseAllFirstLetters(String input) {
        if (isNullOrBlank(input)) {
            return input;
        }
        String[] words = input.split("\\s");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }

}
