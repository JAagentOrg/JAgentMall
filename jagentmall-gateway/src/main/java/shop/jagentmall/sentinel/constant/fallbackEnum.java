package shop.jagentmall.sentinel.constant;

import lombok.Getter;

/**
 * @Title: fallbackEnum
 * @Author: [tianyou]
 * @Date: 2025/2/27
 * @Description: fallback策略枚举类
 */
@Getter
public enum fallbackEnum {
    /**
     * 默认返回fallback
     */
    DEFAULT("default"),
    /**
     * 流量控制前缀
     */
    FLOWLIMIT_PREFIX("flowlimit:"),

    /**
     * 熔断降级前缀
     */
    DEGRADE_PREFIX("degrade:"),
    /**
     * 默认限流
     */
    FLOWLIMIT_DEFAULT("flowLimitDefault"),

    /**
     * 默认熔断降级
     */
    DEGRADE_DEFAULT("degradeDefault"),
    /**
     * 搜索熔断降级
     */
    DEGRADE_SEARCH("jagentmall-search-esproduct"),
    /**
     * 推荐熔断降级
     */
    DEGRADE_HOME_RECOMMEND("jagentmall-home-recommend"),
    /**
     * webpay熔断降级
     */
    DEGRADE_WEBPAY("jagentmall-portal-webpay"),
    /**
     * pay熔断降级
     */
    DEGRADE_PAY("jagentmall-portal-pay");


    private final String type;

    fallbackEnum(String type) {
        this.type = type;
    }
}
