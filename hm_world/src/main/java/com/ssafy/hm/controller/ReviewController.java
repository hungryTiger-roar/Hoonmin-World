package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping("/items/{itemId}/available")
	@Operation(summary = "상품 리뷰 가능 체크")
	public ResponseEntity<Boolean> canAddItemReview(@PathVariable Integer itemId, @RequestParam String userId) {
		return ResponseEntity.ok(reviewService.canAddItemReview(userId, itemId));
	}
	
	@GetMapping("/items/review/{itemReviewId}")
	@Operation(summary = "상품 리뷰 단건 조회")
	public ResponseEntity<ItemReview> getItemReview(@PathVariable Integer itemReviewId) {
		ItemReview review = reviewService.getItemReview(itemReviewId);
		return review != null ? ResponseEntity.ok(review) : ResponseEntity.notFound().build();
	}
	
	@PatchMapping("/items/review/{itemReviewId}")
	@Operation(summary = "상품 리뷰 수정 (평점, 코멘트만 수정)")
	public ResponseEntity<?> updateItemReview(@PathVariable Integer itemReviewId, @RequestBody ItemReview review) {
		review.setItemReviewId(itemReviewId);
		boolean updated = reviewService.updateItemReview(review);
		return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/items/review/{itemReviewId}")
	@Operation(summary = "상품 리뷰 삭제")
	public ResponseEntity<?> deleteItemReview(@PathVariable Integer itemReviewId) {
		boolean deleted = reviewService.deleteItemReview(itemReviewId);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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

	@GetMapping("/attractions/{attId}/available")
	@Operation(summary = "어트랙션 리뷰 가능 체크")
	public ResponseEntity<Boolean> canAddAttractionReview(@PathVariable Integer attId, @RequestParam String userId) {
		return ResponseEntity.ok(reviewService.canAddAttractionReview(userId, attId));
	}

	@GetMapping("/attractions/review/{attReviewId}")
	@Operation(summary = "어트랙션 리뷰 단건 조회")
	public ResponseEntity<AttractionReview> getAttractionReview(@PathVariable Integer attReviewId) {
		AttractionReview review = reviewService.getAttractionReview(attReviewId);
		return review != null ? ResponseEntity.ok(review) : ResponseEntity.notFound().build();
	}

	@PatchMapping("/attractions/review/{attReviewId}")
	@Operation(summary = "어트랙션 리뷰 수정 (평점, 코멘트만 수정)")
	public ResponseEntity<?> updateAttractionReview(@PathVariable Integer attReviewId, @RequestBody AttractionReview review) {
		review.setAttReviewId(attReviewId);
		boolean updated = reviewService.updateAttractionReview(review);
		return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/attractions/review/{attReviewId}")
	@Operation(summary = "어트랙션 리뷰 삭제")
	public ResponseEntity<?> deleteAttractionReview(@PathVariable Integer attReviewId) {
		boolean deleted = reviewService.deleteAttractionReview(attReviewId);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}
