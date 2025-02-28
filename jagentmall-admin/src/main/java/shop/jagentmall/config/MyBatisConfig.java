package shop.jagentmall.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan({"shop.jagentmall.mapper","shop.jagentmall.dao"})
public class MyBatisConfig {
}
