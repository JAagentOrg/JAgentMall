package shop.jagentmall.idempotent.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.aspectj.lang.ProceedingJoinPoint;
import shop.jagentmall.idempotent.annotation.Idempotent;

/**
 * @Title: IdempotentParamWrapper
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 幂等参数包装
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IdempotentParamWrapper {
    /**
     * 幂等注解
     */
    private Idempotent idempotent;

    /**
     * AOP 处理连接点
     */
    private ProceedingJoinPoint joinPoint;

    /**
     * 锁标识
     */
    private String lockKey;
}
