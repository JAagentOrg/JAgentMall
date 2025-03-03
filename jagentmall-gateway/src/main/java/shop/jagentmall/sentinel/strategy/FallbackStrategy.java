package shop.jagentmall.sentinel.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * @Title: FallbackStrategy
 * @Author: [tianyou]
 * @Date: 2025/2/27
 * @Description: fallback策略接口
 */
public interface FallbackStrategy {
    String mark();
    HttpStatus getStatus();
    Map<String, Object> getResponse(ServerWebExchange exchange);
}
