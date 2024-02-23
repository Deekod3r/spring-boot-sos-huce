package com.project.soshuceapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

}
