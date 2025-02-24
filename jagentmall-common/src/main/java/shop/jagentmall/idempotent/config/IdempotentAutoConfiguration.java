package shop.jagentmall.idempotent.config;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import shop.jagentmall.idempotent.core.IdempotentAspect;
import shop.jagentmall.idempotent.core.handler.spel.IdempotentSpELByMQExecuteHandler;
import shop.jagentmall.idempotent.core.handler.spel.IdempotentSpELByRestAPIExecuteHandler;
import shop.jagentmall.idempotent.core.handler.spel.IdempotentSpELService;

/**
 * @Title: IdempotentAutoConfiguration
 * @Author: [tianyou]
 * @Date: 2025/2/23
 * @Description: 幂等自动装配
 */
@Configuration
public class IdempotentAutoConfiguration {
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    /**
     * SpEL 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELService idempotentSpELByRestAPIExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentSpELByRestAPIExecuteHandler(redissonClient);
    }

    /**
     * SpEL 方式幂等实现，基于 MQ 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELByMQExecuteHandler idempotentSpELByMQExecuteHandler(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentSpELByMQExecuteHandler(stringRedisTemplate);
    }
}
