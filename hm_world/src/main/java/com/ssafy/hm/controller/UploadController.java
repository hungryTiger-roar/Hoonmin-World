package com.ssafy.hm.controller;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestParam("upload_file") MultipartFile file,
            HttpServletRequest request
    ) {
        if (!new File(uploadPath).exists()) {
            log.error("Upload path does not exist: {}", uploadPath);
            return ResponseEntity.internalServerError().body("upload path missing");
        }
        String uploadFileOriginalName = file.getOriginalFilename();
        if (uploadFileOriginalName == null || uploadFileOriginalName.isBlank()) {
            return ResponseEntity.badRequest().body("missing file name");
        }

        int dotIndex = uploadFileOriginalName.lastIndexOf('.');
        String fileExtension = dotIndex >= 0 ? uploadFileOriginalName.substring(dotIndex) : "";
        UUID uniqueName = UUID.randomUUID();

        File savedFile = new File(uploadPath + File.separator + uniqueName + fileExtension);
        try {
            file.transferTo(savedFile);
        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.internalServerError().body("upload failed");
        }

        String url = request.getRequestURL().toString();       // http://localhost:8080/upload
        String link = url.substring(0, url.lastIndexOf("/"));  // http://localhost:8080
        String downloadLink = link + "/uploaded/" + uniqueName + fileExtension;

        String message = String.format("{ \"message\": \"upload success\", \"url\": \"%s\" }", downloadLink);
        return ResponseEntity.ok(message);
    }
}
