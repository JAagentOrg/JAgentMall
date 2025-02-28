package shop.jagentmall.idempotent.core.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import shop.jagentmall.idempotent.annotation.Idempotent;
import shop.jagentmall.idempotent.core.domain.IdempotentParamWrapper;

/**
 * @Title: IdempotentExecuteHandler
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 幂等执行处理器
 */
public interface IdempotentExecuteHandler {
    /**
     * 幂等处理逻辑
     *
     * @param wrapper 幂等参数包装器
     */
    void handler(IdempotentParamWrapper wrapper);

    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent);

    /**
     * 异常流程处理
     */
    default void exceptionProcessing() {

    }

    /**
     * 后置处理
     */
    default void postProcessing() {

    }
}
