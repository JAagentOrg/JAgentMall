package shop.jagentmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/07/22:17
 * @Description: 应用门户启动入口
 */
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication(scanBasePackages = "shop.jagentmall")
public class PortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }

}
