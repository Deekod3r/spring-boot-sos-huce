package com.project.soshuceapi.common;

public class ResponseCode {
    
    public static class Common {
        public static final String SUCCESS = "000";
        public static final String FAIL = "001";
        public static final String NOT_FOUND = "002";
        public static final String EXISTED = "003";
        public static final String INVALID = "004";
        public static final String NOT_MATCH = "005";
    }
    
    public static class Authentication {
        public static final String AUTHENTICATION_ERROR = "100";
        public static final String VERIFY_CODE_EXPIRED = "101";
        public static final String VERIFY_CODE_INCORRECT = "102";
        public static final String PERMISSION_DENIED = "103";
    }

    
}
