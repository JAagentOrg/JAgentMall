package shop.jagentmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/07/22:17
 * @Description: 搜索启动入口
 */
@EnableDiscoveryClient
@SpringBootApplication
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}
