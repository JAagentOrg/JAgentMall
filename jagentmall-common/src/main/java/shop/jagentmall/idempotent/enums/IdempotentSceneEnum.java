package shop.jagentmall.idempotent.enums;

/**
 * @Title: IdempotentSceneEnum
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 幂等场景枚举
 */
public enum IdempotentSceneEnum {
    /**
     * 基于 RestAPI 场景验证
     */
    RESTAPI,

    /**
     * 基于 MQ 场景验证
     */
    MQ
}
