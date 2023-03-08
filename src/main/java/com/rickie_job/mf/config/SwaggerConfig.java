package com.rickie_job.mf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


/**
 * 自定义Swagger接口文档
 *
 * @author rickie
 */
//表示这个类是一个配置类,会把这个类注入到ioc容器中
@Configuration
//开启swagger2的功能
@EnableSwagger2WebMvc
//让bean只在某些环境下生效 支持数组
@Profile({"dev","test"})
public class SwaggerConfig {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("接口文档")
                .apiInfo(apiInfo("伙伴匹配中心","1.0"))
                .useDefaultResponseMessages(true)
                .forCodeGeneration(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.rickie_job.mf.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    /**
     * api 信息
     * @return ApiInfo
     */
    //构造器模式 为了方便管理修改配置
    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfoBuilder()
                .title(title)
                .description("伙伴匹配中心接口文档")
                .termsOfServiceUrl("https://github.com/RickieTT")
                .contact(new Contact("rickie","https://github.com/RickieTT","rickieqaq@outlook.com"))
                .version(version)
                .build(); //返回一个ApiInfo对象
    }
}

