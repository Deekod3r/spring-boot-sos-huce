package com.project.soshuceapi.common;

public class ResponseMessage {

    public static class Common {
        public static final String SERVER_ERROR = "Hệ thống đang gặp sự cố, vui lòng thử lại sau";
        public static final String SUCCESS = "Thành công";
    }

    public static class Authentication {
        public static final String VERIFY_CODE_EXPIRED = "Mã xác thực đã hết hạn";
        public static final String VERIFY_CODE_INCORRECT = "Mã xác thực không chính xác";
        public static final String PERMISSION_DENIED = "Không có quyền truy cập";
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
        public static final String MISSING_ROLE = "Thông tin quyền người dùng không được bỏ trống";
        public static final String INVALID_ROLE = "Thông tin quyền người dùng không hợp lệ";
        public static final String USER_EXISTED = "Thông tin người dùng đã tồn tại";
        public static final String NOT_FOUND = "Thông tin người dùng không tồn tại";
        public static final String MISSING_AUTHENTICATION_INFO = "Thông tin xác thực không người dùng được bỏ trống";
        public static final String INVALID_AUTHENTICATION_INFO = "Thông tin xác thực người dùng không hợp lệ";
        public static final String VERIFY_CODE_EXPIRED = "Mã xác thực đã hết hạn";
        public static final String VERIFY_CODE_INCORRECT = "Mã xác thực không chính xác";
        public static final String PERMISSION_DENIED = "Không có quyền truy cập";
        public static final String LOGIN_INFO_INCORRECT = "Thông tin đăng nhập không chính xác";
        public static final String NOT_MATCH = "Thông tin định danh người dùng không khớp";
        public static final String NOT_AVAILABLE_FOR_UPDATE = "Người dùng không trong trạng thái có thể cập nhật";
        public static final String MISSING_OLD_PASSWORD = "Thông tin mật khẩu cũ không được bỏ trống";
        public static final String INVALID_OLD_PASSWORD = "Thông tin mật khẩu cũ không hợp lệ";
        public static final String PASSWORD_DUPLICATE = "Mật khẩu mới không được trùng với mật khẩu cũ";
        public static final String MISSING_STATUS = "Thông tin trạng thái người dùng không được bỏ trống";
        public static final String INVALID_STATUS = "Thông tin trạng thái người dùng không hợp lệ";
        public static final String MISSING_ADDRESS = "Thông tin địa chỉ người dùng không được bỏ trống";


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
        public static final String NOT_FOUND = "Thông tin thú cưng không tồn tại";
        public static final String NOT_MATCH = "Thông tin định danh thú cưng không khớp";
        public static final String NOT_AVAILABLE_FOR_ADOPT = "Thú cưng không trong trạng thái để nhận nuôi";
        public static final String NOT_AVAILABLE_FOR_UPDATE = "Thú cưng không trong trạng thái có thể cập nhật";
        public static final String MISSING_INTAKE_DATE = "Thông tin ngày tiếp nhận không được bỏ trống";
        public static final String INVALID_INTAKE_DATE = "Thông tin ngày tiếp nhận không hợp lệ";

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
        public static final String NOT_FOUND = "Thông tin đơn nhận nuôi không tồn tại";
        public static final String NOT_MATCH = "Thông tin định danh đơn nhận nuôi không khớp";
        public static final String NOT_AVAILABLE_FOR_CANCEL = "Đơn nhận nuôi không trong trạng thái để hủy";
        public static final String MAX_ADOPTS = "Số lượng đơn nhận nuôi đã đạt tối đa";
        public static final String DUPLICATE_ADOPT = "Đã có đơn nhận nuôi cho thú cưng này";
        public static final String MISSING_ID = "Thông tin định danh đơn nhận nuôi không được bỏ trống";
        public static final String MISSING_STATUS = "Thông tin trạng thái đơn nhận nuôi không được bỏ trống";
        public static final String INVALID_STATUS = "Thông tin trạng thái đơn nhận nuôi không hợp lệ";
        public static final String INVALID_SEARCH_DATE = "Thông tin thời gian không hợp lệ";
        public static final String NOT_AVAILABLE_FOR_UPDATE = "Đơn nhận nuôi không trong trạng thái có thể cập nhật";
        public static final String MISSING_FEE = "Thông tin tiền vía nhận nuôi không được bỏ trống";
        public static final String INVALID_FEE = "Thông tin tiền vía nhận nuôi không hợp lệ";
    }

    public static class PetCareLog {
        public static final String MISSING_DATE = "Thông tin ngày kiểm tra không được bỏ trống";
        public static final String INVALID_DATE = "Thông tin ngày kiểm tra không hợp lệ";
        public static final String MISSING_NOTE = "Thông tin ghi chú không được bỏ trống";
        public static final String INVALID_NOTE = "Thông tin ghi chú không hợp lệ";
        public static final String MISSING_ADOPT_ID = "Thông tin định danh đơn nhận nuôi không được bỏ trống";
        public static final String NOT_FOUND = "Thông tin nhật ký không tồn tại";
        public static final String NOT_MATCH = "Thông tin định danh nhật ký không khớp";
        public static final String MISSING_ID = "Thông tin định danh nhật ký không được bỏ trống";
    }

    public static class Galleria {
        public static final String MISSING_TITLE = "Thông tin tiêu đề không được bỏ trống";
        public static final String INVALID_TITLE = "Thông tin tiêu đề không hợp lệ";
        public static final String MISSING_DESCRIPTION = "Thông tin mô tả không được bỏ trống";
        public static final String INVALID_DESCRIPTION = "Thông tin mô tả không hợp lệ";
        public static final String MISSING_IMAGE = "Thông tin hình ảnh không được bỏ trống";
        public static final String MISSING_STATUS = "Thông tin trạng thái không được bỏ trống";
        public static final String INVALID_STATUS = "Thông tin trạng thái không hợp lệ";
        public static final String NOT_FOUND = "Thông tin bộ sưu tập không tồn tại";
        public static final String NOT_MATCH = "Thông tin định danh bộ sưu tập không khớp";
        public static final String MISSING_ID = "Thông tin định danh bộ sưu tập không được bỏ trống";
        public static final String MISSING_LINK = "Thông tin liên kết không được bỏ trống";
        public static final String MISSING_INDEX = "Thông tin thứ tự không được bỏ trống";
        public static final String NOT_AVAILABLE_FOR_UPDATE = "Bộ sưu tập không trong trạng thái có thể cập nhật";
    }

    public static class Location {
        public static final String GET_INFO_PROVINCE_FAIL = "Lấy thông tin tỉnh/thành phố thất bại";
        public static final String GET_INFO_DISTRICT_FAIL = "Lấy thông tin quận/huyện thất bại";
        public static final String GET_INFO_WARD_FAIL = "Lấy thông tin phường/xã thất bại";
    }

    public static class Config {
        public static final String MISSING_KEY = "Thông tin khóa cấu hình không được bỏ trống";
        public static final String INVALID_KEY = "Thông tin khóa cấu hình không hợp lệ";
        public static final String MISSING_DESCRIPTION = "Thông tin mô tả cấu hình không được bỏ trống";
        public static final String INVALID_DESCRIPTION = "Thông tin mô tả cấu hình không hợp lệ";
        public static final String MISSING_VALUE = "Thông tin giá trị cấu hình không được bỏ trống";
        public static final String INVALID_VALUE = "Thông tin giá trị cấu hình không hợp lệ";
        public static final String NOT_FOUND = "Thông tin cấu hình không tồn tại";
        public static final String NOT_MATCH = "Thông tin định danh cấu hình không khớp";
        public static final String MISSING_CONFIG_ID = "Thông tin định danh cấu hình không được bỏ trống";
        public static final String MISSING_CONFIG_VALUE_ID = "Thông tin định danh giá trị cấu hình không được bỏ trống";
        public static final String NOT_AVAILABLE_FOR_UPDATE = "Cấu hình không trong trạng thái có thể cập nhật";
        public static final String INVALID_CONFIG_ID = "Thông tin định danh cấu hình không hợp lệ";
    }

    public static class Bank {
        public static final String MISSING_NAME = "Thông tin tên tài khoản nhận hỗ trợ không được bỏ trống";
        public static final String INVALID_NAME = "Thông tin tên tài khoản nhận hỗ trợ không hợp lệ";
        public static final String MISSING_ACCOUNT_NUMBER = "Thông tin số tài khoản không được bỏ trống";
        public static final String INVALID_ACCOUNT_NUMBER = "Thông tin số tài khoản không hợp lệ";
        public static final String MISSING_OWNER = "Thông tin chủ sở hữu không được bỏ trống";
        public static final String INVALID_OWNER = "Thông tin chủ sở hữu không hợp lệ";
        public static final String MISSING_LOGO = "Thông tin logo không được bỏ trống";
        public static final String MISSING_ID = "Thông tin định danh tài khoản nhận hỗ trợ không được bỏ trống";
        public static final String NOT_FOUND = "Thông tin tài khoản nhận hỗ trợ không tồn tại";
        public static final String NOT_MATCH = "Thông tin định danh tài khoản nhận hỗ trợ không khớp";
        public static final String NOT_AVAILABLE_FOR_UPDATE = "Tài khoản nhận hỗ trợ không trong trạng thái có thể cập nhật";
        public static final String MAX_QUANTITY = "Số lượng tài khoản nhận hỗ trợ đã đạt tối đa";
        public static final String NAME_EXISTED = "Tên tài khoản nhận hỗ trợ đã tồn tại";
    }

    public static class News {
        public static final String MISSING_TITLE = "Thông tin tiêu đề tin tức không được bỏ trống";
        public static final String INVALID_TITLE = "Thông tin tiêu đề tin tức không hợp lệ";
        public static final String MISSING_CONTENT = "Thông tin nội dung tin tức không được bỏ trống";
        public static final String INVALID_CONTENT = "Thông tin nội dung tin tức không hợp lệ";
        public static final String MISSING_IMAGE = "Thông tin hình ảnh tin tức không được bỏ trống";
        public static final String MISSING_CATEGORY = "Thông tin danh mục tin tức không được bỏ trống";
        public static final String NOT_FOUND = "Thông tin tin tức không tồn tại";
        public static final String NOT_MATCH = "Thông tin định danh tin tức không khớp";
        public static final String NOT_AVAILABLE_FOR_UPDATE = "Tin tức không trong trạng thái có thể cập nhật";
        public static final String MISSING_ID = "Thông tin định danh tin tức không được bỏ trống";
        public static final String MISSING_STATUS = "Thông tin trạng thái tin tức không được bỏ trống";
        public static final String INVALID_STATUS = "Thông tin trạng thái tin tức không hợp lệ";
        public static final String TITLE_EXISTED = "Tiêu đề tin tức đã tồn tại";
        public static final String MISSING_DESCRIPTION = "Thông tin mô tả tin tức không được bỏ trống";
    }

    public static class NewsCategory {
        public static final String MISSING_NAME = "Thông tin tên danh mục tin tức không được bỏ trống";
        public static final String INVALID_NAME = "Thông tin tên danh mục tin tức không hợp lệ";
        public static final String MISSING_DESCRIPTION = "Thông tin mô tả danh mục tin tức không được bỏ trống";
        public static final String NOT_FOUND = "Thông tin danh mục tin tức không tồn tại";
        public static final String NOT_MATCH = "Thông tin định danh danh mục tin tức không khớp";
        public static final String NOT_AVAILABLE_FOR_UPDATE = "Danh mục tin tức không trong trạng thái có thể cập nhật";
        public static final String MISSING_ID = "Thông tin định danh danh mục tin tức không được bỏ trống";
        public static final String MISSING_STATUS = "Thông tin trạng thái danh mục tin tức không được bỏ trống";
        public static final String INVALID_STATUS = "Thông tin trạng thái danh mục tin tức không hợp lệ";
        public static final String NAME_EXISTED = "Tên danh mục tin tức đã tồn tại";
    }

}
