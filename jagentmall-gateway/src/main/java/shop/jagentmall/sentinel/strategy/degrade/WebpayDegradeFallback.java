package shop.jagentmall.sentinel.strategy.degrade;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import shop.jagentmall.constant.AuthConstant;
import shop.jagentmall.dto.UserDto;
import shop.jagentmall.sentinel.constant.fallbackEnum;
import shop.jagentmall.sentinel.strategy.FallbackStrategy;
import shop.jagentmall.util.StpMemberUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: WebpayDegradeFallback
 * @Author: [tianyou]
 * @Date: 2025/2/28
 * @Description: 手机支付熔断降级
 */
@Component
@Slf4j
public class WebpayDegradeFallback implements FallbackStrategy {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public String mark() {
        return fallbackEnum.DEGRADE_PREFIX.getType() + fallbackEnum.DEGRADE_WEBPAY.getType();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    @Override
    public Map<String, Object> getResponse(ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDto userDto = (UserDto) StpMemberUtil.getSession().get(AuthConstant.STP_MEMBER_INFO);
            String userId = userDto != null ? String.valueOf(userDto.getId()) : null;

            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            Map<String, String> queryParams = exchange.getRequest().getQueryParams().toSingleValueMap();

            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("path", path);
            requestInfo.put("method", method);
            requestInfo.put("queryParams", queryParams);
            requestInfo.put("userId", userId);

            String jsonRequest = objectMapper.writeValueAsString(requestInfo);
            stringRedisTemplate.opsForList().leftPush("webpay_retry_queue", jsonRequest);

            response.put("code", 200);
            response.put("message", "支付请求处理中，稍后会发送请求结果，请耐心等待");
        } catch (Exception e) {
            log.error(e.getMessage());
            response.put("code", 500);
            response.put("message", "降级失败，请稍后重试");
        }
        return response;
    }
}
