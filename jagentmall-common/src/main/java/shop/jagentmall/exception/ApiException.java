package shop.jagentmall.exception;

import shop.jagentmall.api.IErrorCode;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/08/21:10
 * @Description: 自定义API异常
 */
public class ApiException extends RuntimeException {
    private IErrorCode errorCode;

    public ApiException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
