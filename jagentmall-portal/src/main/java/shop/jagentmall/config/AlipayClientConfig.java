package shop.jagentmall.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Title: AlipayClientConfig
 * @Author: [tianyou]
 * @Date: 2025/2/19
 * @Description: 支付宝客户端配置
 */
@Configuration
public class AlipayClientConfig {
    @Bean
    public AlipayClient alipayClient(AlipayConfig config){
        return new DefaultAlipayClient(config.getGatewayUrl(),config.getAppId(),config.getAppPrivateKey(), config.getFormat(),config.getCharset(),config.getAlipayPublicKey(),config.getSignType());
    }
}
