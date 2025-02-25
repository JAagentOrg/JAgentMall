package shop.jagentmall.constant;

import lombok.Getter;

/**
 * @Title: RedisKeyPrefixEnum
 * @Author: [tianyou]
 * @Date: 2025/2/25
 * @Description: Redis 键的前缀枚举类
 */
@Getter
public enum RedisKeyPrefixEnum {
    /**
     * redis库存缓存前缀key
     */
    ORDER_CHECK_STOCK("order_check_stock:"),

    /**
     * 分布式锁key，用于获取redis库存
     */
    LOCK_STOCK("lock_stock");


    /**
     * 前缀key
     */
    private String prefixKey;
    RedisKeyPrefixEnum(String prefixKey){
        this.prefixKey = prefixKey;
    }
}
