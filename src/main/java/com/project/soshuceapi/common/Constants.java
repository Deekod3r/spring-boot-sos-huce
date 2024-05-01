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
        public final static String KEY_UPDATE_EMAIL_INFO = "-UPDATE-EMAIL-INFO";
        public final static String KEY_UPDATE_EMAIL_CODE = "-UPDATE-EMAIL-CODE";
        public final static String KEY_ROLE = "ROLE_";
        public final static String ROLE_ADMIN = "ADMIN";
        public final static String ROLE_MANAGER = "MANAGER";
        public final static String ROLE_USER = "USER";
        public final static String ROLE_GUEST = "GUEST";
    }

    public static class Donate {
        public final static Integer MONEY = 1;
        public final static Integer GOODS = 2;
        public final static Integer OTHER = 3;
    }

    public static class Regex {
        public final static String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$";
        public final static String CHARACTER = "^[a-zA-ZưứừữựửƯỨỪỮỰỬéèẽẹẻÉÈẼẸẺếềễệểẾỀỄỆỂýỳỹỵỷÝỲỸỴỶúùũụủÚÙŨỤỦíìĩịỉÍÌĨỊỈóòõọỏÓÒÕỌỎôốồỗộổÔỐỒỖỘỔơớờỡợởƠỚỜỠỢỞáàãạảÁÀÃẠẢâấầẫậẩÂẤẦẪẬẨăắằẵặẳĂẮẰẴẶẲđĐ ]{2,}$";
        public final static String DIGIT = "^\\d+$";
    }

    public static class Mail {
        public final static String SUBJECT = "[SOS HUCE] %s";
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
        public final static String FORGOT_PASSWORD_BODY = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #14B8A6;">Forgot Password</h2>
                    <p style="color: #666;">Dear %s,</p>
                    <p style="color: #666;">Bạn đã yêu cầu đặt lại mật khẩu tài khoản HUCE Pet. Dưới đây là mã xác thực <b style="color: #14B8A6;">%s</b>:</p>
                    <div class="verification-code" style="background-color: #14B8A6; color: #fff; padding: 10px; font-size: 18px; border-radius: 4px; margin: 20px 0; display: inline-block;">%s</div>
                    <p style="color: #666;">Mã xác thực sẽ có hiệu lực trong vòng 5 phút kể từ thời điểm bạn nhận được email.</p>
                    <p style="color: #666;">Hãy sử dụng để hoàn thành quá trình đặt lại mật khẩu.</p>
                    <p style="color: #666;">Nếu không phải bạn thực hiện, vui lòng bỏ qua email này.</p>
                </div>
            </html>
            """;
        public final static String UPDATE_EMAIL_BODY = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #14B8A6;">Update Email</h2>
                    <p style="color: #666;">Dear %s,</p>
                    <p style="color: #666;">Bạn đã yêu cầu cập nhật email cho tài khoản HUCE Pet. Dưới đây là mã xác thực <b style="color: #14B8A6;">%s</b>:</p>
                    <div class="verification-code" style="background-color: #14B8A6; color: #fff; padding: 10px; font-size: 18px; border-radius: 4px; margin: 20px 0; display: inline-block;">%s</div>
                    <p style="color: #666;">Mã xác thực sẽ có hiệu lực trong vòng 5 phút kể từ thời điểm bạn nhận được email.</p>
                    <p style="color: #666;">Hãy sử dụng để hoàn thành quá trình cập nhật email.</p>
                    <p style="color: #666;">Nếu không phải bạn thực hiện, vui lòng bỏ qua email này.</p>
                </div>
            </html>
            """;
        public final static String RESET_PASSWORD_BODY = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #14B8A6;">Reset Password</h2>
                    <p style="color: #666;">Dear %s,</p>
                    <p style="color: #666;">Mật khẩu của tài khoản HUCE Pet đã được đặt lại.</p>
                    <p style="color: #666;">Nếu không phải bạn thực hiện, vui lòng liên hệ với chúng tôi ngay.</p>
                </div>
            </html>
            """;
        public final static String REGISTER_ADOPT_SUCCESS = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #14B8A6;">Adopt Success</h2>
                    <p style="color: #666;">Dear %s,</p>
                    <p style="color: #666;">Bạn đã đăng ký nhận nuôi thành công. Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.</p>
                    <p style="color: #666;">Cảm ơn bạn đã tham gia cộng đồng HUCE Pet.</p>
                </div>
            </html>
            """;
        public final static String HEADER = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #14B8A6; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h1 style="color: #fff;">SOS HUCE</h1>
                </div>
            </html>
            """;
        public final static String FOOTER = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <p style="color: #666;">Trân trọng,</p>
                    <p style="color: #666;">HUCE Pet</p>
                </div>
            </html>
            """;
    }

    public static class ActionLog {
        public final static String CREATE = "CREATE";
        public final static String UPDATE = "UPDATE";
        public final static String DELETE = "DELETE";
        public final static String DELETE_SOFT = "DELETE_SOFT";
        public final static String LOCK = "LOCK";
        public final static String UNLOCK = "UNLOCK";
        public final static String LOGIN = "LOGIN";
    }

    public static class AdoptStatus {
        public final static Integer WAIT_FOR_PROGRESSING = 1;
        public final static Integer IN_PROGRESS = 2;
        public final static Integer REJECT = 3;
        public final static Integer CANCEL = 4;
        public final static Integer COMPLETE = 5;
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

    public static class FormatPattern {
        public final static String LOCAL_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";
        public final static String LOCAL_DATETIME_WITH_NANOSECONDS  = "yyyy-MM-dd HH:mm:ss.SSSSSS";
        public final static String LOCAL_DATE = "yyyy-MM-dd";
    }

    public static class Bank {
        public final static int MAX_QUANTITY = 4;
    }

}
