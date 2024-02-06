package com.project.soshuceapi.common;

public class ResponseMessage {

    public static class Common {
        public static final String SERVER_ERROR = "server.error";
        public static final String SUCCESS = "%.success";
        public static final String FAIL = "%.fail";
        public static final String ERROR = "%.error";
        public static final String NOT_FOUND = "%.not.found";
        public static final String EXISTED = "%.existed";
        public static final String EMPTY = "%.empty";
        public static final String NOT_MATCH = "%.not.match";
        public static final String INVALID_INPUT = "%.invalid.input";
    }

    public static class Authentication {
        public static final String AUTHENTICATION_ERROR = "%.authentication.error";
        public static final String VERIFY_CODE_EXPIRED = "%.verify.code.expired";
        public static final String VERIFY_CODE_INCORRECT = "%.verify.code.incorrect";
        public static final String PERMISSION_DENIED = "%.permission.denied";
    }

}
