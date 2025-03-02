package shop.jagentmall.sentinel.strategy.degrade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import shop.jagentmall.sentinel.constant.fallbackEnum;
import shop.jagentmall.sentinel.strategy.FallbackStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: HomeRecommendDegradeFallback
 * @Author: [tianyou]
 * @Date: 2025/2/27
 * @Description: 首页推荐服务降级
 */
@Component
public class HomeRecommendDegradeFallback implements FallbackStrategy {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public String mark() {
        return fallbackEnum.DEGRADE_PREFIX.getType() + fallbackEnum.DEGRADE_HOME_RECOMMEND.getType();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    @Override
    public Map<String, Object> getResponse(ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        // 尝试从 Redis 获取默认搜索结果
        String cachedResult = stringRedisTemplate.opsForValue().get("home_recommend:default_results");

        if (cachedResult != null && !cachedResult.isEmpty()) {
            // 如果缓存命中，返回默认结果
            response.put("code", 200); // 使用 200 表示成功返回降级数据
            response.put("message", "推荐服务不可用，使用默认结果");
            response.put("data", cachedResult); // 返回 JSON 字符串
        } else {
            // 缓存未命中，返回基本提示
            response.put("code", 503);
            response.put("message", "推荐服务不可用，无默认结果可用");
        }
        return response;
    }
}
