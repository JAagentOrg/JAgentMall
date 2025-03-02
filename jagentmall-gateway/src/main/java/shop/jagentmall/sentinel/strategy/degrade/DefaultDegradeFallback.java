package shop.jagentmall.sentinel.strategy.degrade;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import shop.jagentmall.sentinel.constant.fallbackEnum;
import shop.jagentmall.sentinel.strategy.FallbackStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: DefaultDegradeFallback
 * @Author: [tianyou]
 * @Date: 2025/2/27
 * @Description: 默认熔断降级fallback
 */
@Component
public class DefaultDegradeFallback implements FallbackStrategy {
    @Override
    public String mark() {
        return fallbackEnum.DEGRADE_PREFIX.getType() + fallbackEnum.DEGRADE_DEFAULT.getType();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    @Override
    public Map<String, Object> getResponse(ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("message", "服务暂时不可用，请稍后重试");
        return response;
    }
}
