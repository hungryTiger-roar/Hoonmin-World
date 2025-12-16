package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.hm.dto.HomeImage;
import com.ssafy.hm.service.HomeImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/home-images")
@Tag(name = "홈 이미지", description = "홈 배너 이미지 관리")
public class HomeImageController {

	private final HomeImageService homeImageService;

	public HomeImageController(HomeImageService homeImageService) {
		this.homeImageService = homeImageService;
	}

	@GetMapping
	@Operation(summary = "홈 이미지 전체 조회")
	public ResponseEntity<List<HomeImage>> list() {
		return ResponseEntity.ok(homeImageService.getAll());
	}

	@PostMapping
	@Operation(summary = "홈 이미지 등록")
	public ResponseEntity<?> create(@RequestBody HomeImage image) {
		return homeImageService.create(image)
				? ResponseEntity.ok(image)
				: ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{homeId}")
	@Operation(summary = "홈 이미지 삭제")
	public ResponseEntity<Void> delete(@PathVariable Integer homeId) {
		return homeImageService.remove(homeId)
				? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}
}
