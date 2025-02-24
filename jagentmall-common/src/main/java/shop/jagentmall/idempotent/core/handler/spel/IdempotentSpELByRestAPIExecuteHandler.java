package shop.jagentmall.idempotent.core.handler.spel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import shop.jagentmall.idempotent.annotation.Idempotent;
import shop.jagentmall.idempotent.core.IdempotentAspect;
import shop.jagentmall.idempotent.core.IdempotentContext;
import shop.jagentmall.idempotent.core.domain.IdempotentParamWrapper;
import shop.jagentmall.idempotent.core.handler.AbstractIdempotentExecuteHandler;
import shop.jagentmall.idempotent.core.handler.exception.ClientException;
import shop.jagentmall.idempotent.toolkit.SpELUtil;

/**
 * @Title: IdempotentSpELByRestAPIExecuteHandler
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 基于 SpEL 方法验证请求幂等性，适用于 RestAPI 场景
 */
@RequiredArgsConstructor
public class IdempotentSpELByRestAPIExecuteHandler  extends AbstractIdempotentExecuteHandler implements IdempotentSpELService {
    private final RedissonClient redissonClient;

    private final static String LOCK = "lock:spEL:restAPI";

    @SneakyThrows
    @Override
    public IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        // 获取被拦截方法上面的注解
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        // 解析 SpEL 表达式
        String key = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        // 返回注解包装类实例
        return IdempotentParamWrapper.builder()
                .lockKey(key)
                .joinPoint(joinPoint)
                .build();
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        // 获取前缀+解析后的spEL表达式作为key
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        // 获取对应的锁
        RLock lock = redissonClient.getLock(uniqueKey);
        // 尝试加锁，如果加锁失败，则抛出异常
        if (!lock.tryLock()) {
            throw new ClientException(wrapper.getIdempotent().message());
        }
        // 获取成功则将锁放入上下文中
        IdempotentContext.put(LOCK, lock);
    }

    @Override
    public void postProcessing() {
        // 后置处理，解锁
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public void exceptionProcessing() {
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }
}
