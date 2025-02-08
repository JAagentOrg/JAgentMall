package shop.jagentmall.annotation;

import java.lang.annotation.*;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/08/21:10
 * @Description: 自定义注解，有该注解的缓存方法会抛出异常
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheException {
}
