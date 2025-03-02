package shop.jagentmall.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import shop.jagentmall.domain.AliPayParam;
import shop.jagentmall.service.AlipayService;
import shop.jagentmall.service.NotificationService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Title: PayRetryTask
 * @Author: [tianyou]
 * @Date: 2025/2/28
 * @Description: 支付延迟定时任务
 */
@Component
@Slf4j
public class PayRetryTask {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlipayService alipayService;

    @Scheduled(fixedRate = 10000) // 每 10 秒执行一次
    public void retryWebpayRequests() {
        while (true) {
            String requestJson = stringRedisTemplate.opsForList().rightPop("webpay_retry_queue");
//            log.info("检查下APP端延迟支付.......");
            if (requestJson == null) break;

            try {
                // 反序列化请求信息
                Map<String, Object> requestInfo = objectMapper.readValue(requestJson, Map.class);
                Map<String, String> queryParams = (Map<String, String>) requestInfo.get("queryParams");
                String userId = (String) requestInfo.get("userId");

                // 重建 AliPayParam
                AliPayParam aliPayParam = new AliPayParam();
                Long orderId = Long.valueOf(queryParams.get("orderId"));

                // 直接调用本地支付服务
                String paymentUrl = alipayService.getWebpaymentUrl(orderId);

                // 提取支付链接
//                String paymentUrl = extractPaymentUrl(paymentResult);
                if (paymentUrl != null && userId != null) {
                    notificationService.notifyUser(userId, "您的支付请求已准备好，请点击完成支付：" + paymentUrl);
                }

                System.out.println("重试成功: " + requestInfo.get("path"));
            } catch (Exception e) {
                stringRedisTemplate.opsForList().leftPush("webpay_retry_queue", requestJson);
                System.err.println("重试失败: " + e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 10000) // 每 10 秒执行一次
    public void retrypayRequests() {
        while (true) {
            String requestJson = stringRedisTemplate.opsForList().rightPop("pay_retry_queue");
//            log.info("检查下网页端延迟支付.......");
            if (requestJson == null) break;

            try {
                // 反序列化请求信息
                Map<String, Object> requestInfo = objectMapper.readValue(requestJson, Map.class);
                Map<String, String> queryParams = (Map<String, String>) requestInfo.get("queryParams");
                String userId = (String) requestInfo.get("userId");

                Long orderId = Long.valueOf(queryParams.get("orderId"));

                // 直接调用本地支付服务
                String paymentUrl = alipayService.getPaymentUrl(orderId);

                // 提取支付链接
//                String paymentUrl = extractPaymentUrl(paymentResult);
//                log.info("===============paymentUrl:{}",paymentUrl);
                if (paymentUrl != null && userId != null) {
                    notificationService.notifyUser(userId, "您的支付请求已准备好，请点击完成支付：" + paymentUrl);
                }

                System.out.println("重试成功: " + requestInfo.get("path"));
            } catch (Exception e) {
                stringRedisTemplate.opsForList().leftPush("webpay_retry_queue", requestJson);
                System.err.println("重试失败: " + e.getMessage());
            }
        }
    }

    private String extractPaymentUrl(String paymentResult) {
        try {
            return paymentResult.contains("action=\"") ?
                    paymentResult.split("action=\"")[1].split("\"")[0] : null;
        } catch (Exception e) {
            return null;
        }
    }
}
