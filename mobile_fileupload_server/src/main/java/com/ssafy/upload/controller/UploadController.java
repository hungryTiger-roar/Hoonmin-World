package com.ssafy.upload.controller;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@CrossOrigin("*")
public class UploadController {
	
	private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Value("${uploadPath}")
    String uploadPath;
    
    
	@PostMapping("/upload")
	public String upload(@RequestParam("upload_file") MultipartFile file, HttpServletRequest request) {
		if(new File(uploadPath).exists() == false) {
			log.error("업로드 폴더가 존재하지 않습니다. ");
			return "업로드 실패";
		}
		
		String uploadFileOriginalName = file.getOriginalFilename(); //업로드된 파일 이름.
		long size = file.getSize(); //파일 사이즈
		
		log.debug("파일명 : {}", uploadFileOriginalName);
		log.debug("파일 사이즈(byte) : {}", size);

		String fileExtension = uploadFileOriginalName.substring(uploadFileOriginalName.lastIndexOf("."),uploadFileOriginalName.length());
		
		
		/*
		  같은 파일명이 있을경우 오류. 랜덤파일명 생성을 위해서...
		  만약 원래이름을 사용하려면 uuid로 변환하지 않고 진행. 
		 */
		
		UUID uniqueName = UUID.randomUUID();
		log.debug("변경된 이름 : {}", uniqueName);
		log.debug("확장자 : {}", fileExtension);
		
		
		
		// 파일 건수가 많아지면 yyyy/mm/dd 형태로 일자별로 하위 폴더를 만들어서 저장하기도 한다.
		
//		 File saveFile = new File(uploadPath + File.separator + fileRealName); // uuid 적용하지 않으려면..
		File savedFile = new File(uploadPath + File.separator + uniqueName + fileExtension); 
		try {
			file.transferTo(savedFile); // 업로드 된 파일을 지정한 경로/이름으로 저장.
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("저장된 파일 : {}", savedFile);

		//아래는 단순히 다운로드할 수 있는 경로 만들어서 보여주려고...
		String url = request.getRequestURL().toString();       // http://localhost:8080/upload
		String link = url.substring(0, url.lastIndexOf("/"));  // http://localhost:8080
		String downloadLink = link + "/uploaded/"+uniqueName + fileExtension;

		return "{ \"message\" : \"업로드 성공.<br><br>"
				+ "<a href="+downloadLink+">"+ downloadLink+"</a><br><br>"
				+ "에서 다운로드 할 수 있습니다. \"}";
	}
}