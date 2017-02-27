package com.newt;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication 
@EnableDiscoveryClient 
@EnableSwagger2
public class CustomerServiceApplication extends WebMvcConfigurerAdapter{
	private static final Logger logger = Logger.getLogger(CustomerServiceApplication.class);
	 @Override
	    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	        configurer.favorPathExtension(false);
	        configurer.defaultContentType(MediaType.APPLICATION_JSON);
	    }  

	public static void main(String[] args) {
		logger.info("CustomerServices invokation...");
		SpringApplication.run(CustomerServiceApplication.class, args);
	}
@Bean
public Docket api() {                
    return new Docket(DocumentationType.SWAGGER_2)          
      .select()
      .apis(RequestHandlerSelectors.basePackage("com.newt.controller"))
      .build()
      .apiInfo(apiInfo());
}

private ApiInfo apiInfo() {
    ApiInfo apiInfo = new ApiInfo(
      "Customer Microservice",
      "Provides services to add customer and query customer data",
      "V.1.0",
      "Terms of service",
      "devopsinabox@newtglobal.com",
      "",
      "");
    return apiInfo;
}

}


