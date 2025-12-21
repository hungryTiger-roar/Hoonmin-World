package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.hm.dto.BuyImage;
import com.ssafy.hm.service.BuyImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/buy-images")
@Tag(name = "구매 캐러셀 이미지", description = "구매 페이지 캐러셀 이미지 관리")
public class BuyImageController {

	private final BuyImageService buyImageService;

	public BuyImageController(BuyImageService buyImageService) {
		this.buyImageService = buyImageService;
	}

	@GetMapping
	@Operation(summary = "구매 캐러셀 이미지 목록 조회")
	public ResponseEntity<List<BuyImage>> list() {
		return ResponseEntity.ok(buyImageService.getAll());
	}

	@PostMapping
	@Operation(summary = "구매 캐러셀 이미지 등록")
	public ResponseEntity<?> create(@RequestBody BuyImage image) {
		return buyImageService.create(image)
				? ResponseEntity.ok(image)
				: ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{buyId}")
	@Operation(summary = "구매 캐러셀 이미지 삭제")
	public ResponseEntity<Void> delete(@PathVariable Integer buyId) {
		return buyImageService.remove(buyId)
				? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}
}
