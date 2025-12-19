package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ssafy.hm.dto.Attraction;
import com.ssafy.hm.service.AttractionService;

@RestController
@RequestMapping("/attractions")
@Tag(name = "attractions", description = "attraction management")
public class AttractionController {

	private final AttractionService attractionService;

	public AttractionController(AttractionService attractionService) {
		this.attractionService = attractionService;
	}

	@GetMapping
	@Operation(summary = "list attractions")
	public ResponseEntity<List<Attraction>> list() {
		return ResponseEntity.ok(attractionService.getAll());
	}

	@GetMapping("/{attId}")
	@Operation(summary = "get attraction")
	public ResponseEntity<Attraction> get(@PathVariable Integer attId) {
		Attraction attraction = attractionService.get(attId);
		return attraction != null ? ResponseEntity.ok(attraction) : ResponseEntity.notFound().build();
	}

	@PostMapping
	@Operation(summary = "create attraction")
	public ResponseEntity<?> create(@RequestBody Attraction attraction) {
		boolean created = attractionService.create(attraction);
		return created ? ResponseEntity.ok(attraction) : ResponseEntity.badRequest().build();
	}

	@PutMapping("/{attId}")
	@Operation(summary = "update attraction")
	public ResponseEntity<?> update(@PathVariable Integer attId, @RequestBody Attraction attraction) {
		attraction.setAttId(attId);
		boolean updated = attractionService.update(attraction);
		return updated ? ResponseEntity.ok(attraction) : ResponseEntity.notFound().build();
	}

	@PatchMapping("/{attId}/able")
	@Operation(summary = "toggle attraction availability")
	public ResponseEntity<?> updateAble(@PathVariable Integer attId) {
		Attraction attraction = new Attraction();
		attraction.setAttId(attId);

		boolean updated = attractionService.updateAble(attraction);
		return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

	@PatchMapping("/{attId}/category")
	@Operation(summary = "update attraction category")
	public ResponseEntity<?> updateCategory(@PathVariable Integer attId, @RequestBody Attraction attraction) {
		attraction.setAttId(attId);
		boolean updated = attractionService.updateCategory(attraction);

		return updated ? ResponseEntity.ok(attraction) : ResponseEntity.notFound().build();
	}

	@PatchMapping("/{attId}/total")
	@Operation(summary = "increase attraction total")
	public ResponseEntity<?> updateTotal(@PathVariable Integer attId, @RequestBody Attraction attraction) {
		attraction.setAttId(attId);
		boolean updated = attractionService.updateTotal(attraction);

		return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{attId}")
	@Operation(summary = "delete attraction")
	public ResponseEntity<Void> delete(@PathVariable Integer attId) {
		boolean deleted = attractionService.remove(attId);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}
