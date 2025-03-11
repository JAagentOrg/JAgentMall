package shop.jagentmall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClient;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/08/22:52
 * @Description:
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "shop.jagentmall")
public class AiApplicationMain {

    private static final Logger logger = LoggerFactory.getLogger(AiApplicationMain.class);
    public static void main(String[] args) {
        SpringApplication.run(AiApplicationMain.class, args);
    }

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(EmbeddingModel embeddingModel, VectorStore vectorStore,
                                                       @Value("classpath:rag/terms-of-service.txt") Resource termsOfServiceDocs) {

        return args -> {
            // Ingest the document into the vector store
            vectorStore.write(new TokenTextSplitter().transform(new TextReader(termsOfServiceDocs).read()));

            vectorStore.similaritySearch("Cancelling Bookings").forEach(doc -> {
                logger.info("Similar Document: {}", doc.getContent());
            });
        };
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}