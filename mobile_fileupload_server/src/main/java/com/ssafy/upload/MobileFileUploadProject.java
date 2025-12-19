package com.ssafy.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;


/**
 * !!! 파일업로드 샘플. !!!
 * 
 * 1. dependency 추가
 * 2. application.properties에 저장할 경로 설정
 * 3. ResourceHandler에 url 과 저장된 경로 매핑
 * 4. upload.html 파일에서 post+enctype을 multipart로 파일 업로드. 
 * 5. controler에서 업로드 파일 저장. 
 */



@SpringBootApplication
public class MobileFileUploadProject {

	public static void main(String[] args) {
		SpringApplication.run(MobileFileUploadProject.class, args);
	}
	
}
