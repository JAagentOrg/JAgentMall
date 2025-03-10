package shop.jagentmall.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/03
 * @Description: MyBatis相关配置
 */
@Configuration
@MapperScan("shop.jagentmall.mapper")
public class MyBatisConfig {
}
