package shop.jagentmall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import shop.jagentmall.api.CommonPage;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.domain.OmsOrderDetail;
import shop.jagentmall.fegin.AiPortalFeignClient;
import shop.jagentmall.service.impl.CustomerSupportAssistant;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/03
 * @Description: AI助理
 */
@Tag(name = "AssistantController", description = "AI助理")
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final CustomerSupportAssistant agent;

    public AssistantController(CustomerSupportAssistant agent) {
        this.agent = agent;
    }

    @Operation(summary = "AI助理")
    @GetMapping(path="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(String chatId, String userMessage) {
        return agent.chat(chatId, userMessage);
    }


}
