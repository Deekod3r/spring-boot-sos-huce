package com.project.soshuceapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.soshuceapi.common.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.Date;

public class DataUtil {

    public static <T> String toJSON(T t) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(t);
        } catch (Exception e) {
            throw new RuntimeException("Error when converting " + t.getClass().getName() +" to JSON", e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error when converting JSON to " + clazz.getName(), e);
        }
    }

    public static String getIP() {
        InetAddress IP = null;
        try {
            IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        } catch (UnknownHostException e) {
            return "UNKNOWN";
        }
    }

    public static boolean isDate(String dateTimeStr, String formatPattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            formatter.parse(dateTimeStr);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static Date parseDate(String dateStr) {
        try {
            if (StringUtil.isNullOrBlank(dateStr)) {
                return null;
            }
            if (isDate(dateStr, Constants.FormatPattern.LOCAL_DATE)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FormatPattern.LOCAL_DATE);
                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                return Date.valueOf(localDate);
            }
            return null;
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Error when parsing date", e);
        }
    }

    public static LocalDate parseLocalDate(String dateStr) {
        try {
            if (StringUtil.isNullOrBlank(dateStr)) {
                return null;
            }
            if (isDate(dateStr, Constants.FormatPattern.LOCAL_DATE)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FormatPattern.LOCAL_DATE);
                return LocalDate.parse(dateStr, formatter);
            }
            return null;
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Error when parsing date", e);
        }
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeStr) {
        try {
            if (StringUtil.isNullOrBlank(dateTimeStr)) {
                return null;
            }
            if (isDate(dateTimeStr, Constants.FormatPattern.LOCAL_DATETIME)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FormatPattern.LOCAL_DATETIME);
                return LocalDateTime.parse(dateTimeStr, formatter);
            }
            return null;
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Error when parsing date", e);
        }
    }

    public static Timestamp parseTimestamp(String dateTimeStr) {
        try {
            if (StringUtil.isNullOrBlank(dateTimeStr)) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FormatPattern.LOCAL_DATETIME);
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            return Timestamp.valueOf(localDateTime);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Error when parsing timestamp", e);
        }
    }
}
