package shop.jagentmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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
    public static void main(String[] args) {
        SpringApplication.run(AiApplicationMain.class, args);
    }
}