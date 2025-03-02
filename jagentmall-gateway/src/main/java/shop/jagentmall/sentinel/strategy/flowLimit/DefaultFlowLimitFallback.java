package shop.jagentmall.sentinel.strategy.flowLimit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import shop.jagentmall.sentinel.constant.fallbackEnum;
import shop.jagentmall.sentinel.strategy.FallbackStrategy;

import java.util.HashMap;
import java.util.Map;
/**
 * @Title: DefaultFlowLimitFallback
 * @Author: [tianyou]
 * @Date: 2025/2/27
 * @Description: 默认限流fallback
 */
@Component
public class DefaultFlowLimitFallback implements FallbackStrategy {
    @Override
    public String mark() {
        return fallbackEnum.FLOWLIMIT_PREFIX.getType() + fallbackEnum.FLOWLIMIT_DEFAULT.getType();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }

    @Override
    public Map<String, Object> getResponse(ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 429);
        response.put("message", "您操作的太快了，请稍等！");
        return response;
    }
}
