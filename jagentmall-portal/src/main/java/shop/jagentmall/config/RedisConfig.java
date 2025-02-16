package shop.jagentmall.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * @Title: RedisConfig
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: Redis配置文件
 */
@EnableCaching
@Configuration
public class RedisConfig extends BaseRedisConfig {
}
