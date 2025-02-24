package shop.jagentmall.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import shop.jagentmall.idempotent.annotation.Idempotent;
import shop.jagentmall.idempotent.core.handler.IdempotentExecuteHandler;
import shop.jagentmall.idempotent.core.handler.IdempotentExecuteHandlerFactory;
import shop.jagentmall.idempotent.core.handler.exception.RepeatConsumptionException;

import java.lang.reflect.Method;

/**
 * @Title: IdempotentAspect
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 幂等注解AOP拦截器
 */
@Aspect
public class IdempotentAspect {
    /**
     * 增强方法标记 {@link Idempotent} 注解逻辑
     */
    @Around("@annotation(shop.jagentmall.idempotent.annotation.Idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取切点
        Idempotent idempotent = getIdempotent(joinPoint);
        // 从工厂中获取应该使用的类
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());
        Object resultObj;
        try {
            // 执行前置方法
            // 具体流程为：获取注解上的参数，并且解析，然后封装成为一个幂等参数包裹；执行handler函数，构建锁key，然后获取锁，并将锁放入到该线程的ThreadLocal中。
            instance.execute(joinPoint, idempotent);
            // 继续执行被拦截的方法
            resultObj = joinPoint.proceed();
            // 执行后置方法
            instance.postProcessing();
        } catch (RepeatConsumptionException ex) {
            /**
             * 触发幂等逻辑时可能有两种情况：
             *    * 1. 消息还在处理，但是不确定是否执行成功，那么需要返回错误，方便 MQ 再次通过重试队列投递
             *    * 2. 消息处理成功了，该消息直接返回成功即可
             */
            if (!ex.getError()) {
                return null;
            }
            throw ex;
        } catch (Throwable ex) {
            // 客户端消费存在异常，需要删除幂等标识方便下次 MQ 再次通过重试队列投递
            instance.exceptionProcessing();
            throw ex;
        } finally {
            // 清除幂等上下文，即清除创建出来的ThreadLocal
            IdempotentContext.clean();
        }
        return resultObj;
    }

    public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        // 获取被拦截方法的方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 为什么不直接使用methodSignature.getMethod()获取方法，而是通过反射重新获取方法？
        // 因为methodSignature.getMethod()不一定能获取到真正实现的方法，而有可能获取到接口的方法或者父类的方法，所以需要通过反射重新获取
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        // 获取目标方法上的 @Idempotent 注解实例
        // 可以获取到注解上的参数
        return targetMethod.getAnnotation(Idempotent.class);
    }

}
