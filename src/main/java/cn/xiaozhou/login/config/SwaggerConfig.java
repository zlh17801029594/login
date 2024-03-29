package cn.xiaozhou.login.config;

import cn.xiaozhou.login.filter.TokenFilter;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        ParameterBuilder builder = new ParameterBuilder();
        builder.parameterType("header").name(TokenFilter.TOKEN_KEY)
                .description("header参数")
                .required(false)
                .modelRef(new ModelRef("string"));
        return new Docket(DocumentationType.SWAGGER_2).groupName("swagger接口文档")
                .apiInfo(new ApiInfoBuilder().title("swagger接口文档")
                        .contact(new Contact("小周", "", ""))
                        .version("1.0")
                        .build())
                .globalOperationParameters(Lists.newArrayList(builder.build()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.xiaozhou.login.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
