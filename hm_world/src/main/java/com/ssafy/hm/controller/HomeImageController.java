package com.ssafy.hm.controller;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.hm.dto.HomeImage;
import com.ssafy.hm.service.HomeImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/home-images")
@Tag(name = "home-images", description = "Home carousel images")
public class HomeImageController {

    private final HomeImageService homeImageService;

    @Value("${uploadPath}")
    private String uploadPath;

    public HomeImageController(HomeImageService homeImageService) {
        this.homeImageService = homeImageService;
    }

    @GetMapping
    @Operation(summary = "List home images")
    public ResponseEntity<List<HomeImage>> list() {
        return ResponseEntity.ok(homeImageService.getAll());
    }

    @PostMapping
    @Operation(summary = "Create home image")
    public ResponseEntity<?> create(@RequestBody HomeImage image) {
        return homeImageService.create(image)
                ? ResponseEntity.ok(image)
                : ResponseEntity.badRequest().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image and create home image")
    public ResponseEntity<?> uploadAndCreate(
            @RequestParam("upload_file") MultipartFile file,
            HttpServletRequest request
    ) {
        String uploadFileOriginalName = file.getOriginalFilename();
        if (uploadFileOriginalName == null || uploadFileOriginalName.isBlank()) {
            return ResponseEntity.badRequest().body("missing file name");
        }
        String fileExtension = uploadFileOriginalName.substring(uploadFileOriginalName.lastIndexOf("."));
        UUID uniqueName = UUID.randomUUID();
        File savedFile = new File(uploadPath + File.separator + uniqueName + fileExtension);
        try {
            file.transferTo(savedFile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("upload failed");
        }

        String url = request.getRequestURL().toString();       // http://localhost:8080/home-images
        String link = url.substring(0, url.lastIndexOf("/"));  // http://localhost:8080
        String downloadLink = link + "/uploaded/" + uniqueName + fileExtension;

        HomeImage image = new HomeImage(null, downloadLink);
        return homeImageService.create(image)
                ? ResponseEntity.ok(image)
                : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{homeId}")
    @Operation(summary = "Delete home image")
    public ResponseEntity<Void> delete(@PathVariable Integer homeId) {
        return homeImageService.remove(homeId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}