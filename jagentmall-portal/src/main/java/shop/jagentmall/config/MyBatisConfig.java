package shop.jagentmall.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Title: MyBatisConfig
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: mybatis配置文件
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"shop.jagentmall.mapper","shop.jagentmall.dao"})
public class MyBatisConfig {
}
