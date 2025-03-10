package shop.jagentmall.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/03
 * @Description: SpringDoc相关配置
 */
@Configuration
public class SpringDocConfig implements WebMvcConfigurer {
    private static final String SECURITY_SCHEME_NAME = "Authorization";

    @Bean
    public OpenAPI feiginOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("jagentmall认证中心")
                        .description("jagentmall认证中心相关接口文档")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")
                                .url("https://github.com/JAagentOrg/JAgentMall")))
                .externalDocs(new ExternalDocumentation()
                        .description("抖音电商")
                        .url("http://www.jagentmall.shop"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //配置访问`/swagger-ui/`路径时可以直接跳转到`/swagger-ui/index.html`
        registry.addViewController("/swagger-ui/").setViewName("redirect:/swagger-ui/index.html");
    }

    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        //解决Knife4j配置认证后无法自动添加认证头的问题
        return openApi -> {
            //全局添加鉴权参数
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    pathItem.readOperations().forEach(operation -> {
                        operation.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
                    });
                });
            }
        };
    }

}
