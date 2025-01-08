// package com.hch.chat_simple.config;

// // import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import springfox.documentation.builders.ApiInfoBuilder;
// import springfox.documentation.builders.PathSelectors;
// import springfox.documentation.builders.RequestHandlerSelectors;
// import springfox.documentation.service.ApiInfo;
// import springfox.documentation.spi.DocumentationType;
// import springfox.documentation.spring.web.plugins.Docket;
// import springfox.documentation.swagger2.annotations.EnableSwagger2;


// @EnableSwagger2
// @Configuration
// // @EnableSwaggerBootstrapUI
// public class SwaggerConfigure {

//     @Bean
//     public Docket createRestApi() {
//         return new Docket(DocumentationType.SWAGGER_2)
//                 .apiInfo(apiInfo())
//                 .select()
//                 .apis(RequestHandlerSelectors.basePackage("com.hch.chat_simple.controller"))
//                 .paths(PathSelectors.any())
//                 .build()
//                 .groupName("chat swagger");
//     }

//     private ApiInfo apiInfo() {
//         return new ApiInfoBuilder()
//                 .title("聊天简单实现")
//                 .termsOfServiceUrl("")
//                 .version("1.0")
//                 .build();
//     }

// }

