package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ssafy.hm.dto.AttractionReview;
import com.ssafy.hm.dto.ItemReview;
import com.ssafy.hm.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@Tag(name = "리뷰", description = "상품/어트랙션 리뷰 관리")
public class ReviewController {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@PostMapping("/items")
	@Operation(summary = "상품 리뷰 작성")
	public ResponseEntity<?> addItemReview(@RequestBody ItemReview review) {
		boolean created = reviewService.addItemReview(review);
		return created ? ResponseEntity.ok(review) : ResponseEntity.badRequest().build();
	}

	@GetMapping("/items/{itemId}")
	@Operation(summary = "상품 리뷰 조회")
	public ResponseEntity<List<ItemReview>> getItemReviews(@PathVariable Integer itemId) {
		return ResponseEntity.ok(reviewService.getItemReviews(itemId));
	}

	@PostMapping("/attractions")
	@Operation(summary = "어트랙션 리뷰 작성")
	public ResponseEntity<?> addAttractionReview(@RequestBody AttractionReview review) {
		boolean created = reviewService.addAttractionReview(review);
		return created ? ResponseEntity.ok(review) : ResponseEntity.badRequest().build();
	}

	@GetMapping("/attractions/{attId}")
	@Operation(summary = "어트랙션 리뷰 조회")
	public ResponseEntity<List<AttractionReview>> getAttractionReviews(@PathVariable Integer attId) {
		return ResponseEntity.ok(reviewService.getAttractionReviews(attId));
	}
}
