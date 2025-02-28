package shop.jagentmall.idempotent.core.handler;

import shop.jagentmall.context.SpringContextHolder;
import shop.jagentmall.idempotent.core.handler.spel.IdempotentSpELByMQExecuteHandler;
import shop.jagentmall.idempotent.core.handler.spel.IdempotentSpELByRestAPIExecuteHandler;
import shop.jagentmall.idempotent.enums.IdempotentSceneEnum;
import shop.jagentmall.idempotent.enums.IdempotentTypeEnum;

/**
 * @Title: IdempotentExecuteHandlerFactory
 * @Author: [tianyou]
 * @Date: 2025/2/23
 * @Description:
 */
public final class IdempotentExecuteHandlerFactory {
    public static IdempotentExecuteHandler getInstance(IdempotentSceneEnum scene, IdempotentTypeEnum type) {
        IdempotentExecuteHandler result = null;
        switch (scene) {
            case RESTAPI -> {
                switch (type) {
//                    case PARAM -> result = ApplicationContextHolder.getBean(IdempotentParamService.class);
//                    case TOKEN -> result = ApplicationContextHolder.getBean(IdempotentTokenService.class);
                    case SPEL -> result = SpringContextHolder.getBean(IdempotentSpELByRestAPIExecuteHandler.class);
                    default -> {
                    }
                }
            }
            case MQ -> result = SpringContextHolder.getBean(IdempotentSpELByMQExecuteHandler.class);
            default -> {
            }
        }
        return result;
    }
}
