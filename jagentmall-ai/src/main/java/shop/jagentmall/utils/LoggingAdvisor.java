package shop.jagentmall.utils;

import org.springframework.ai.chat.client.RequestResponseAdvisor;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;

import java.util.Map;

/**
 * 将聊天信息存储 utils
 */
public class LoggingAdvisor implements RequestResponseAdvisor {

	@Override
	public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
		System.out.println("\nRequest: " + request);
		return request;
	}

    @Override
    public int getOrder() {
        return 0;
    }
}
