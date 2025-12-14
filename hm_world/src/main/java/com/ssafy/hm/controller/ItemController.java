package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ssafy.hm.dto.Item;
import com.ssafy.hm.service.ItemService;

@RestController
@RequestMapping("/items")
@Tag(name = "상품", description = "기프트샵 상품 관리")
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@GetMapping
	@Operation(summary = "상품 전체 조회")
	public ResponseEntity<List<Item>> list() {
		return ResponseEntity.ok(itemService.getAll());
	}

	@GetMapping("/{itemId}")
	@Operation(summary = "상품 단건 조회")
	public ResponseEntity<Item> get(@PathVariable Integer itemId) {
		Item item = itemService.get(itemId);
		return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
	}

	@PostMapping
	@Operation(summary = "상품 등록")
	public ResponseEntity<?> create(@RequestBody Item item) {
		boolean created = itemService.create(item);
		return created ? ResponseEntity.ok(item) : ResponseEntity.badRequest().build();
	}

	@PutMapping("/{itemId}")
	@Operation(summary = "상품 수정")
	public ResponseEntity<?> update(@PathVariable Integer itemId, @RequestBody Item item) {
		item.setItemId(itemId);
		boolean updated = itemService.update(item);
		return updated ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{itemId}")
	@Operation(summary = "상품 삭제")
	public ResponseEntity<Void> delete(@PathVariable Integer itemId) {
		boolean deleted = itemService.remove(itemId);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}
