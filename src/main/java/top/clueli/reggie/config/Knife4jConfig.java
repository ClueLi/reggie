package top.clueli.reggie.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class Knife4jConfig {

    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.OAS_30)
                // 创建接口文档的具体信息
                .apiInfo(webApiInfo())
                .enable(true)
                // 创建选择器，控制哪些接口被加入文档
                .select()
                // 指定@ApiOperation标注的接口被加入文档
                .apis(RequestHandlerSelectors.basePackage("top.clueli.reggie.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    // 创建接口文档的具体信息，会显示在接口文档页面中
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                // 文档标题
                .title("瑞吉外卖系统接口文档")
                // 文档描述
                .description("本文档描述了瑞吉外卖系统的接口定义")
                // 版本
                .version("1.0")
                // 联系人信息
                .contact(new Contact("clueli", "http://clueli.top", "445571153@qq.com"))
                .build();
    }

}
