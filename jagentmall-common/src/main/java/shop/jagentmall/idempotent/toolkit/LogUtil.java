package shop.jagentmall.idempotent.toolkit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: LogUtil
 * @Author: [tianyou]
 * @Date: 2025/2/22
 * @Description: 日志工具类
 */
public class LogUtil {
    /**
     * 获取 Logger
     */
    public static Logger getLog(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return LoggerFactory.getLogger(methodSignature.getDeclaringType());
    }
}
