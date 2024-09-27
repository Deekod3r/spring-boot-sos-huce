package com.project.soshuceapi.common;

public class Constants {

    private Constants() {
    }

    public static class Security {
        private Security() {
        }

        public static final Long TOKEN_EXPIRATION_TIME = 60 * 60 * 1000L;  // (đơn vị mls) 1 giờ
        public static final Long TOKEN_REFRESH_EXPIRATION_TIME = 3 * 24 * 60 * 60 * 1000L;  // (đơn vị mls) 3 ngày
        public static final Long VERIFICATION_EXPIRATION_TIME = 5 * 60L;  // (đơn vị s) 5 phút
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String REQUEST_HEADER_AUTH = "Authorization";
        public static final String TOKEN_HEADER_KEY = "TOKEN_AUTH_";
        public static final Integer VERIFY_CODE_LENGTH = 6;
    }

    public static class User {
        private User() {
        }

        public static final String KEY_REGISTER_INFO = "-REGISTER-INFO";
        public static final String KEY_REGISTER_CODE = "-REGISTER-CODE";
        public static final String KEY_FORGOT_PASSWORD_CODE = "-FORGOT-PASSWORD-CODE";
        public static final String KEY_UPDATE_EMAIL_INFO = "-UPDATE-EMAIL-INFO";
        public static final String KEY_UPDATE_EMAIL_CODE = "-UPDATE-EMAIL-CODE";
        public static final String KEY_ROLE = "ROLE_";
        public static final String ROLE_ADMIN = "ADMIN";
        public static final String ROLE_MANAGER = "MANAGER";
        public static final String ROLE_USER = "USER";
        public static final String ROLE_GUEST = "GUEST";
    }

    public static class Donate {
        private Donate() {
        }

        public static final Integer MONEY = 1;
        public static final Integer GOODS = 2;
        public static final Integer OTHER = 3;
    }

    public static class Regex {
        private Regex() {
        }

        public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$";
        public static final String CHARACTER = "^[a-zA-ZưứừữựửƯỨỪỮỰỬéèẽẹẻÉÈẼẸẺếềễệểẾỀỄỆỂýỳỹỵỷÝỲỸỴỶúùũụủÚÙŨỤỦíìĩịỉÍÌĨỊỈóòõọỏÓÒÕỌỎôốồỗộổÔỐỒỖỘỔơớờỡợởƠỚỜỠỢỞáàãạảÁÀÃẠẢâấầẫậẩÂẤẦẪẬẨăắằẵặẳĂẮẰẴẶẲđĐ ]{2,}$";
        public static final String DIGIT = "^\\d+$";
    }

    public static class Mail {
        private Mail() {
        }

        public static final String SUBJECT = "[SOS HUCE] %s";
        public static final String VERIFY_BODY = """
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
        public static final String FORGOT_PASSWORD_BODY = """
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
        public static final String UPDATE_EMAIL_BODY = """
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
        public static final String RESET_PASSWORD_BODY = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #14B8A6;">Reset Password</h2>
                    <p style="color: #666;">Dear %s,</p>
                    <p style="color: #666;">Mật khẩu của tài khoản HUCE Pet đã được đặt lại.</p>
                    <p style="color: #666;">Nếu không phải bạn thực hiện, vui lòng liên hệ với chúng tôi ngay.</p>
                </div>
            </html>
            """;
        public static final String REGISTER_ADOPT_SUCCESS = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #14B8A6;">Adopt Success</h2>
                    <p style="color: #666;">Dear %s,</p>
                    <p style="color: #666;">Bạn đã đăng ký nhận nuôi thành công. Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.</p>
                    <p style="color: #666;">Cảm ơn bạn đã tham gia cộng đồng HUCE Pet.</p>
                </div>
            </html>
            """;
        public static final String HEADER = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #14B8A6; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <h1 style="color: #fff;">SOS HUCE</h1>
                </div>
            </html>
            """;
        public static final String FOOTER = """
            <html>
                <div class="container" style="max-width: 600px; margin: 50px auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                    <p style="color: #666;">Trân trọng,</p>
                    <p style="color: #666;">HUCE Pet</p>
                </div>
            </html>
            """;
    }

    public static class ActionLog {
        private ActionLog() {
        }

        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String DELETE_SOFT = "DELETE_SOFT";
        public static final String LOCK = "LOCK";
        public static final String UNLOCK = "UNLOCK";
        public static final String LOGIN = "LOGIN";
    }

    public static class AdoptStatus {
        private AdoptStatus() {
        }

        public static final Integer WAIT_FOR_PROGRESSING = 1;
        public static final Integer IN_PROGRESS = 2;
        public static final Integer REJECT = 3;
        public static final Integer CANCEL = 4;
        public static final Integer COMPLETE = 5;
    }

    public static class PetStatus {
        private PetStatus() {
        }

        public static final int DIED = 1;
        public static final int ADOPTED = 2;
        public static final int HEALING = 3;
        public static final int WAIT_FOR_ADOPTING = 4;
    }

    public static class ObjectType {
        private ObjectType() {
        }

        public static final String PET = "PET";
        public static final String TREATMENT = "TREATMENT";
        public static final String ADOPT = "ADOPT";
    }

    public static class FormatPattern {
        private FormatPattern() {
        }

        public static final String LOCAL_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";
        public static final String LOCAL_DATETIME_WITH_NANOSECONDS  = "yyyy-MM-dd HH:mm:ss.SSSSSS";
        public static final String LOCAL_DATE = "yyyy-MM-dd";
    }

    public static class Bank {
        private Bank() {
        }

        public static final int MAX_QUANTITY = 4;
    }

}
