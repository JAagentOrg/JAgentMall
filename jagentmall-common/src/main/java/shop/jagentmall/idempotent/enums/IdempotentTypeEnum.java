package shop.jagentmall.idempotent.enums;

/**
 * @Title: IdempotentTypeEnum
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 幂等验证类型枚举
 */
public enum IdempotentTypeEnum {
    /**
     * 基于 Token 方式验证
     */
    TOKEN,

    /**
     * 基于方法参数方式验证
     */
    PARAM,

    /**
     * 基于 SpEL 表达式方式验证
     */
    SPEL
}
