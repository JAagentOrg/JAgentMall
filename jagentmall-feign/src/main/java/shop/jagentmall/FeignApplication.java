package shop.jagentmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/07/22:17
 * @Description: 应用启动入口
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FeignApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeignApplication.class, args);
	}
}
