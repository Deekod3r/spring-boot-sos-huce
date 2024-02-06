package com.project.soshuceapi.common;

public class ResponseCode {
    
    public static class Common {
        public static final String SUCCESS = "00";
        public static final String FAIL = "01";
        public static final String NOT_FOUND = "02";
        public static final String EXISTED = "03";
        public static final String INVALID = "04";
        public static final String NOT_MATCH = "05";
        public static final String EMPTY = "06";

    }
    
    public static class Authentication {
        public static final String INVALID_CREDENTIALS = "05";
        public static final String AUTHENTICATION_ERROR = "06";
        public static final String VERIFY_CODE_EXPIRED = "07";
        public static final String VERIFY_CODE_INCORRECT = "08";
        public static final String PERMISSION_DENIED = "09";
    }
    
}
