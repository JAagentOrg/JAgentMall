package shop.jagentmall.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import shop.jagentmall.sentinel.strategy.FallbackStrategySelector;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: GatewayConfig
 * @Author: [tianyou]
 * @Date: 2025/2/26
 * @Description:
 */
@Configuration
public class SentinelGatewayConfig {
    @Autowired
    private FallbackStrategySelector strategySelector;
    /**
     * 自定义限流处理器
     */
    @PostConstruct
    public void initBlockHandlers() {
        BlockRequestHandler blockHandler = (exchange, throwable) -> {
            var strategy = strategySelector.select(exchange, throwable);
            return ServerResponse.status(strategy.getStatus())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(BodyInserters.fromValue(strategy.getResponse(exchange)));
        };
        GatewayCallbackManager.setBlockHandler(blockHandler);
    }
}
