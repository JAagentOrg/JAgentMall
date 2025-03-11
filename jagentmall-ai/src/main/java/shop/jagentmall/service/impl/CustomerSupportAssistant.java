/*
 * Copyright 2024-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shop.jagentmall.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import shop.jagentmall.utils.LoggingAdvisor;

import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/05
 * @Description: ai客户支持助理
 */
@Service
public class CustomerSupportAssistant {

	private final ChatClient chatClient;

	public CustomerSupportAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory) {

		// @formatter:off
		this.chatClient = modelBuilder
				.defaultSystem("""
						您是“抖音”电商公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
						您正在通过在线聊天系统与客户互动。
						您能够支持已有订单查询查询、商品自动下单、订单取消等操作，其余功能将在后续版本中添加，如果用户问的问题不支持请告知详情。
					   在提供有关订单查询查询、、订单取消等操作之前，您必须始终从用户处获取以下信息：昵称。
					   在提供有关商品自动下单、订单取消等操作之前，您必须始终从用户处获取以下信息：昵称，商品名称。
					   在询问用户之前，请检查消息历史记录以获取昵称、商品名称等信息，尽量避免重复询问给用户造成困扰。
					   在订单取消之前，您必须确保条款允许这样做。
					   如果更改需要收费，您必须在继续之前征得用户同意。
					   使用提供的功能获取预订详细信息、更改预订和取消预订。
					   如果需要，您可以调用相应函数辅助完成。
					   请讲中文。
					   今天的日期是 {current_date}.
					""")
				.defaultAdvisors(
						new PromptChatMemoryAdvisor(chatMemory), // Chat Memory
						new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().topK(4).similarityThresholdAll().build()), // RAG
						new LoggingAdvisor())
				.defaultFunctions("orderList","postShoppingCartOrderRequest") // FUNCTION CALLING
				.build();
		// @formatter:on
	}

	public Flux<String> chat(String chatId, String userMessageContent) {

		return this.chatClient.prompt()
			.system(s -> s.param("current_date", LocalDate.now().toString()))
			.user(userMessageContent)
			.advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
			.stream()
			.content();
	}

}