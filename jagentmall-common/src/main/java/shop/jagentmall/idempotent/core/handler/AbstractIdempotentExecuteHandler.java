package shop.jagentmall.idempotent.core.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import shop.jagentmall.idempotent.annotation.Idempotent;
import shop.jagentmall.idempotent.core.domain.IdempotentParamWrapper;

/**
 * @Title: AbstractIdempotentExecuteHandler
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 幂等执行抽象类
 */
public abstract class AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler{
    /**
     * 构建幂等验证过程中所需要的参数包装器
     *
     * @param joinPoint AOP 方法处理
     * @return 幂等参数包装器
     */
    public abstract IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint);

    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    public void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        // 模板方法模式：构建幂等参数包装器
        IdempotentParamWrapper idempotentParamWrapper = buildWrapper(joinPoint).setIdempotent(idempotent);
        handler(idempotentParamWrapper);
    }
}
