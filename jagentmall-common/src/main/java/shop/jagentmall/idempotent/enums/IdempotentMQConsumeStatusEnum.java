package shop.jagentmall.idempotent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @Title: IdempotentMQConsumeStatusEnum
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 幂等MQ消费状态枚举
 */
@RequiredArgsConstructor
@Getter
public enum IdempotentMQConsumeStatusEnum {
    /**
     * 消费中
     */
    CONSUMING("0"),

    /**
     * 已消费
     */
    CONSUMED("1");

    private final String code;

    /**
     * 如果消费状态等于消费中，返回失败
     *
     * @param consumeStatus 消费状态
     * @return 是否消费失败
     */
    public static boolean isError(String consumeStatus) {
        return Objects.equals(CONSUMING.code, consumeStatus);
    }
}
