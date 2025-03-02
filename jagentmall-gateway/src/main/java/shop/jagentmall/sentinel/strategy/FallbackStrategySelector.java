package shop.jagentmall.sentinel.strategy;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import shop.jagentmall.sentinel.constant.fallbackEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: FallbackStrategySelector
 * @Author: [tianyou]
 * @Date: 2025/2/27
 * @Description: fallback策略选择器
 */
@Slf4j
@Component
public class FallbackStrategySelector implements ApplicationListener<ContextRefreshedEvent> {
    private final Map<String, FallbackStrategy> strategiesMap = new HashMap<>();
    public FallbackStrategy select(ServerWebExchange exchange, Throwable throwable) {
        Route route = (Route) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route.getId();
        if (throwable instanceof FlowException) {
            String key = fallbackEnum.FLOWLIMIT_PREFIX.getType() + routeId;
            FallbackStrategy strategy = strategiesMap.get(key);
            if(strategy == null){
                return strategiesMap.get(fallbackEnum.FLOWLIMIT_PREFIX.getType() + fallbackEnum.FLOWLIMIT_DEFAULT.getType());
            }
            else{
                return strategy;
            }
        } else if (throwable instanceof DegradeException) {
            String key = fallbackEnum.DEGRADE_PREFIX.getType() + routeId;
            log.error("触发熔断降级的key:{}",key);
            FallbackStrategy strategy = strategiesMap.get(key);
            if(strategy == null){
                return strategiesMap.get(fallbackEnum.DEGRADE_PREFIX.getType() + fallbackEnum.DEGRADE_DEFAULT.getType());
            }
            else{
                return strategy;
            }
        }
        return strategiesMap.get(fallbackEnum.DEFAULT.getType());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, FallbackStrategy> actual = event.getApplicationContext().getBeansOfType(FallbackStrategy.class);
        actual.forEach((beanName, bean) -> {
            FallbackStrategy beanExist = strategiesMap.get(bean.mark());
            if (beanExist != null && !beanExist.equals(bean)) { // 避免重复引用同一实例
                throw new RuntimeException(String.format("[%s] Duplicate fallback strategy", bean.mark()));
            }
            strategiesMap.put(bean.mark(), bean);
        });
    }
}
