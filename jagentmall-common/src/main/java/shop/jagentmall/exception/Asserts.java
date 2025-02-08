package shop.jagentmall.exception;

import shop.jagentmall.api.IErrorCode;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/08/21:10
 * @Description: 断言处理类，用于抛出各种API异常
 */
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }
}
