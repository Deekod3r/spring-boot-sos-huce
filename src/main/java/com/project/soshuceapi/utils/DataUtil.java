package com.project.soshuceapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.soshuceapi.common.Constants;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.Date;
import java.util.Objects;

public class DataUtil {

    private DataUtil() {
    }

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
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            return ip.getHostAddress();
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
            return null;
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

    public static LocalDateTime parseLocalDateTime(String dateTimeStr, String formatPattern) {
        try {
            if (StringUtil.isNullOrBlank(dateTimeStr)) {
                return null;
            }
            if (isDate(dateTimeStr, formatPattern)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
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

    public static String parseString(Object obj) {
        return Objects.isNull(obj) ? null : obj.toString();
    }

    public static Integer parseInteger(Object obj) {
        return Objects.isNull(obj) ? null : Integer.parseInt(obj.toString());
    }

    public static Long parseLong(Object obj) {
        return Objects.isNull(obj) ? null : Long.parseLong(obj.toString());
    }

    public static Double parseDouble(Object obj) {
        return Objects.isNull(obj) ? null : Double.parseDouble(obj.toString());
    }

    public static Boolean parseBoolean(Object obj) {
        return Objects.isNull(obj) ? null : Boolean.parseBoolean(obj.toString());
    }

    public static Date parseDate(Object obj) {
        return Objects.isNull(obj) ? null : Date.valueOf(obj.toString());
    }

    public static LocalDate parseLocalDate(Object obj) {
        return Objects.isNull(obj) ? null : LocalDate.parse(obj.toString());
    }

    public static LocalDateTime parseLocalDateTime(Object obj) {
        return Objects.isNull(obj) ? null : LocalDateTime.parse(obj.toString());
    }

    public static Timestamp parseTimestamp(Object obj) {
        return Objects.isNull(obj) ? null : Timestamp.valueOf(obj.toString());
    }

    public static String parseString(Object obj, String defaultValue) {
        return Objects.isNull(obj) ? defaultValue : obj.toString();
    }

    public static Integer parseInteger(Object obj, Integer defaultValue) {
        return Objects.isNull(obj) ? defaultValue : Integer.parseInt(obj.toString());
    }

    public static Long parseLong(Object obj, Long defaultValue) {
        return Objects.isNull(obj) ? defaultValue : Long.parseLong(obj.toString());
    }

    public static Double parseDouble(Object obj, Double defaultValue) {
        return Objects.isNull(obj) ? defaultValue : Double.parseDouble(obj.toString());
    }

    public static Boolean parseBoolean(Object obj, Boolean defaultValue) {
        return Objects.isNull(obj) ? defaultValue : Boolean.parseBoolean(obj.toString());
    }

    public static Date parseDate(Object obj, Date defaultValue) {
        return Objects.isNull(obj) ? defaultValue : Date.valueOf(obj.toString());
    }

    public static LocalDate parseLocalDate(Object obj, LocalDate defaultValue) {
        return Objects.isNull(obj) ? defaultValue : LocalDate.parse(obj.toString());
    }

    public static LocalDateTime parseLocalDateTime(Object obj, LocalDateTime defaultValue) {
        return Objects.isNull(obj) ? defaultValue : LocalDateTime.parse(obj.toString());
    }

    public static Timestamp parseTimestamp(Object obj, Timestamp defaultValue) {
        return Objects.isNull(obj) ? defaultValue : Timestamp.valueOf(obj.toString());
    }

    public static BigDecimal parseBigDecimal(Object obj) {
        return Objects.isNull(obj) ? null : new BigDecimal(obj.toString());
    }

}
