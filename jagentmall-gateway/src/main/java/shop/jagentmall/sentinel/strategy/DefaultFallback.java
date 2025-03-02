package shop.jagentmall.sentinel.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import shop.jagentmall.sentinel.constant.fallbackEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: DefaultFallback
 * @Author: [tianyou]
 * @Date: 2025/2/27
 * @Description: 默认fallback
 */
@Component
public class DefaultFallback implements FallbackStrategy{
    @Override
    public String mark() {
        return fallbackEnum.DEFAULT.getType();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public Map<String, Object> getResponse(ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "Bad Request!");
        return response;
    }
}
