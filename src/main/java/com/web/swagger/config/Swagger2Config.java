package com.web.swagger.config;


import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
@EnableKnife4j
@EnableWebMvc
@ComponentScan(basePackages = {"com.web.controllers"})// 扫描路径
public class Swagger2Config implements WebMvcConfigurer {
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
			.apiInfo(groupApiInfo())
			.groupName("默认接口")
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.web.controllers"))
			.paths(PathSelectors.any())
			.build();
	}


	private ApiInfo groupApiInfo() {
		return new ApiInfoBuilder()
			.title("Java Web Final 文档")
			.description("Java Web Final APIs")
			.termsOfServiceUrl("no URL")
			.contact(new Contact("NXYBW", "requester.com", "ihateemail@email.com"))
			.version("1.0")
			.build();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
	}
}