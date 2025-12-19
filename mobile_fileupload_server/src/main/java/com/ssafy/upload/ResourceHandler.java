package com.ssafy.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceHandler implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceHandler.class);

    @Value("${uploadPath}")
    String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	logger.info("uploadPath : {}", uploadPath);
    	//uploaded로 요청이 올 때 static/uploaded와 properties에 저장된 uploadPath에서 찾는다. 
    	registry.addResourceHandler("/uploaded/**").addResourceLocations( "classpath:/static/uploaded/", "file:///" + uploadPath);
    	
//        registry.addResourceHandler("/img/**")
//                .addResourceLocations("classpath:/img/")
//                .setCachePeriod(20)
//                ;
    }
}
