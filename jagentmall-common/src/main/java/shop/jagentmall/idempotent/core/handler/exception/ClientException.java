package shop.jagentmall.idempotent.core.handler.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import shop.jagentmall.api.IErrorCode;

import java.io.Serializable;
import java.util.Optional;

/**
 * @Title: ClientException
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 客户端异常
 */
@Getter
public class ClientException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L; // 序列化版本号
    public final String errorCode; // 错误码
    public final String errorMessage; // 错误信息

    // 默认构造函数
    public ClientException() {
        this.errorCode = "UNKNOWN";
        this.errorMessage = "An unknown client error occurred.";
    }

    // 带错误信息的构造函数
    public ClientException(String errorMessage) {
        this("UNKNOWN", errorMessage);
    }

    // 带错误码和错误信息的构造函数
    public ClientException(String errorCode, String errorMessage) {
        super("ErrorCode: " + errorCode + ", ErrorMessage: " + errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // 带错误码、错误信息和原因的构造函数
    public ClientException(String errorCode, String errorMessage, Throwable cause) {
        super("ErrorCode: " + errorCode + ", ErrorMessage: " + errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // 获取错误码
    public String getErrorCode() {
        return errorCode;
    }

    // 获取错误信息
    public String getErrorMessage() {
        return errorMessage;
    }

    // 重写 toString 方法，提供更详细的异常信息
    @Override
    public String toString() {
        return "ClientException{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    // 重写 getMessage 方法，提供更友好的异常信息
    @Override
    public String getMessage() {
        return "ClientException: ErrorCode=" + errorCode + ", ErrorMessage=" + errorMessage;
    }
}
