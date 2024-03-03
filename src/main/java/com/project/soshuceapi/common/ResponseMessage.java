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

    public static class User {
        public static final String MISSING_PHONE_NUMBER = "Thông tin số điện thoaại không được bỏ trống";
        public static final String INVALID_PHONE_NUMBER = "Thông tin số điện thoại không hợp lệ";
        public static final String MISSING_NAME = "Thông tin họ và tên không được bỏ trống";
        public static final String INVALID_NAME = "Thông tin họ và tên không hợp lệ";
        public static final String MISSING_EMAIL = "Thông tin email không được bỏ trống";
        public static final String INVALID_EMAIL = "Thông tin email không hợp lệ";
        public static final String MISSING_PASSWORD = "Thông tin mật khẩu không được bỏ trống";
        public static final String INVALID_PASSWORD = "Thông tin mật khẩu không hợp lệ";
        public static final String MISSING_CONFIRM_PASSWORD = "Thông tin xác nhận mật khẩu không được bỏ trống";
        public static final String INVALID_CONFIRM_PASSWORD = "Thông tin xác nhận mật khẩu không hợp lệ";
        public static final String PASSWORD_NOT_MATCH = "Mật khẩu và xác nhận mật khẩu không khớp";
        public static final String MISSING_VERIFY_CODE = "Mã xác nhận không được bỏ trống";
        public static final String INVALID_VERIFY_CODE = "Mã xác nhận không hợp lệ";
        public static final String MISSING_ID = "Thông tin định danh người dùng không được bỏ trống";
    }

    public static class Pet {
        public static final String MISSING_NAME = "Thông tin tên thú cưng không được bỏ trống";
        public static final String INVALID_NAME = "Thông tin tên thú cưng không hợp lệ";
        public static final String MISSING_TYPE = "Thông tin loại thú cưng không được bỏ trống";
        public static final String INVALID_TYPE = "Thông tin loại thú cưng không hợp lệ";
        public static final String MISSING_BREED = "Thông tin giống thú cưng không được bỏ trống";
        public static final String INVALID_BREED = "Thông tin giống thú cưng không hợp lệ";
        public static final String MISSING_COLOR = "Thông tin màu sắc thú cưng không được bỏ trống";
        public static final String INVALID_COLOR = "Thông tin màu sắc thú cưng không hợp lệ";
        public static final String MISSING_AGE = "Thông tin tuổi thú cưng không được bỏ trống";
        public static final String INVALID_AGE = "Thông tin tuổi thú cưng không hợp lệ";
        public static final String MISSING_GENDER = "Thông tin giới tính thú cưng không được bỏ trống";
        public static final String INVALID_GENDER = "Thông tin giới tính thú cưng không hợp lệ";
        public static final String MISSING_IMAGE = "Thông tin hình ảnh thú cưng không được bỏ trống";
        public static final String MISSING_STATUS = "Thông tin trạng thái thú cưng không được bỏ trống";
        public static final String INVALID_STATUS = "Thông tin trạng thái thú cưng không hợp lệ";
        public static final String MISSING_WEIGHT = "Thông tin cân nặng thú cưng không được bỏ trống";
        public static final String INVALID_WEIGHT = "Thông tin cân nặng thú cưng không hợp lệ";
        public static final String MISSING_VACCINE = "Thông tin tiêm vaccine thú cưng không được bỏ trống";
        public static final String INVALID_VACCINE = "Thông tin tiêm vaccine thú cưng không hợp lệ";
        public static final String MISSING_STERILIZATION = "Thông tin triệt sản thú cưng không được bỏ trống";
        public static final String INVALID_STERILIZATION = "Thông tin triệt sản thú cưng không hợp lệ";
        public static final String MISSING_DIET = "Thông tin chế độ ăn thú cưng không được bỏ trống";
        public static final String INVALID_DIET = "Thông tin chế độ ăn thú cưng không hợp lệ";
        public static final String MISSING_RABIES = "Thông tin tiêm phòng dại thú cưng không được bỏ trống";
        public static final String INVALID_RABIES = "Thông tin tiêm phòng dại thú cưng không hợp lệ";
        public static final String MISSING_TOILET = "Thông tin vệ sinh đúng chỗ thú cưng không được bỏ trống";
        public static final String INVALID_TOILET = "Thông tin vệ sinh đúng chỗ thú cưng không hợp lệ";
        public static final String MISSING_FRIENDLY_TO_HUMAN = "Thông tin thân thiện với người không được bỏ trống";
        public static final String INVALID_FRIENDLY_TO_HUMAN = "Thông tin thân thiện với người không hợp lệ";
        public static final String MISSING_FRIENDLY_TO_DOGS = "Thông tin thân thiện với chó không được bỏ trống";
        public static final String INVALID_FRIENDLY_TO_DOGS = "Thông tin thân thiện với chó không hợp lệ";
        public static final String MISSING_FRIENDLY_TO_CATS = "Thông tin thân thiện với mèo không được bỏ trống";
        public static final String INVALID_FRIENDLY_TO_CATS = "Thông tin thân thiện với mèo không hợp lệ";
        public static final String MISSING_DESCRIPTION = "Thông tin mô tả thú cưng không được bỏ trống";
        public static final String MISSING_ID = "Thông tin định danh thú cưng không được bỏ trống";
    }

    public static class Adopt {
        public static final String MISSING_WARD_ID = "Thông tin phường/xã không được bỏ trống";
        public static final String MISSING_DISTRICT_ID = "Thông tin quận/huyện không được bỏ trống";
        public static final String MISSING_PROVINCE_ID = "Thông tin tỉnh/thành phố không được bỏ trống";
        public static final String MISSING_ADDRESS = "Thông tin địa chỉ không được bỏ trống";
        public static final String INVALID_ADDRESS = "Thông tin địa chỉ không hợp lệ";
        public static final String MISSING_REASON = "Thông tin lý do không được bỏ trống";
        public static final String INVALID_REASON = "Thông tin lý do không hợp lệ";
        public static final String MISSING_REGISTERED_BY = "Thông tin người đăng ký không được bỏ trống";
    }

}
