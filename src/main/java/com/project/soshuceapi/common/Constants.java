package com.project.soshuceapi.common;

public class Constants {

    //authentications
    public static class Security {
        public final static Long TOKEN_EXPIRATION_TIME = 60 * 60 * 1000L;  // (đơn vị mls) 1 giờ
        public final static Long TOKEN_REFRESH_EXPIRATION_TIME = 3 * 24 * 60 * 60 * 1000L;  // (đơn vị mls) 3 ngày
        public final static Long VERIFICATION_EXPIRATION_TIME = 5 * 60L;  // (đơn vị s) 5 phút
        public final static String TOKEN_PREFIX = "Bearer ";
        public final static String REQUEST_HEADER_AUTH = "Authorization";
        public final static String TOKEN_HEADER_KEY = "TOKEN_AUTH_";
        public final static Integer VERIFY_CODE_LENGTH = 6;
    }

    public static class User {
        public final static String KEY_REGISTER_INFO = "-REGISTER-INFO";
        public final static String KEY_REGISTER_CODE = "-REGISTER-CODE";
        public final static String KEY_FORGOT_PASSWORD_CODE = "-FORGOT-PASSWORD-CODE";
    }

    //regex register
    public static class Regex {
        public final static String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$";
        public final static String NAME = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưỂỄỆẾỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệếỉịọỏốồổỗộớờởỡợụủứừửữựỳỵỷỹ ]{2,}$";
        public final static String PHONE_NUMBER = "^\\d+$";
    }

    // mail
    public static class Mail {
        public final static String VERIFY_BODY = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #14B8A6;">Email Verification</h2>
                    <p style="color: #666;">Dear %s,</p>
                    <p style="color: #666;">Cảm ơn vì đã tham gia cộng đồng HUCE Pet. Dưới đây là mã xác thực <b style="color: #14B8A6;">%s</b>:</p>
                    <div class="verification-code" style="background-color: #14B8A6; color: #fff; padding: 10px; font-size: 18px; border-radius: 4px; margin: 20px 0; display: inline-block;">%s</div>
                    <p style="color: #666;">Mã xác thực sẽ có hiệu lực trong vòng 5 phút kể từ thời điểm bạn nhận được email.</p>
                    <p style="color: #666;">Hãy sử dụng để hoàn thành quá trình xác thực.</p>
                    <p style="color: #666;">Nếu không phải bạn thực hiện, vui lòng bỏ qua email này.</p>
                </div>
            </html>
            """;
        public final static String SUBJECT = "[SOS HUCE] %s";
    }

    public static class ActionLog {
        public final static String CREATE = "CREATE";
        public final static String UPDATE = "UPDATE";
        public final static String DELETE = "DELETE";
        public final static String DELETE_SOFT = "DELETE_SOFT";
        public final static String LOCK = "LOCK";
        public final static String UNLOCK = "UNLOCK";
    }

    public static class AdoptStatus {
        public final static Integer WAIT_FOR_PROGRESSING = 1;
        public final static Integer IN_PROGRESS = 2;
        public final static Integer REJECT = 3;
        public final static Integer CANCEL = 4;
        public final static Integer COMPLETE = 5;
        public final static Integer RETURN = 6;
    }

    public static class PetStatus {
        public final static int DIED = 1;
        public final static int ADOPTED = 2;
        public final static int HEALING = 3;
        public final static int WAIT_FOR_ADOPTING = 4;
    }

    public static class ObjectType {
        public final static String PET = "PET";
        public final static String TREATMENT = "TREATMENT";
        public final static String ADOPT = "ADOPT";
    }
}
