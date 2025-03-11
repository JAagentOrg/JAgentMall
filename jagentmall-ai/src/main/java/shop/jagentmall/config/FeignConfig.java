package shop.jagentmall.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.jagentmall.component.FeignRequestInterceptor;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/03
 * @Description:
 */
@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor();
    }
}

