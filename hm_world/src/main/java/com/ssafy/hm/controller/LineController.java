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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ssafy.hm.dto.AttractionLine;
import com.ssafy.hm.dto.FriendLine;
import com.ssafy.hm.service.LineService;

@RestController
@RequestMapping("/lines")
@Tag(name = "줄서기", description = "어트랙션 대기열 및 친구 합류")
public class LineController {

	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	@Operation(summary = "대기열 참여")
	public ResponseEntity<?> join(@RequestBody AttractionLine line) {
		boolean joined = lineService.joinLine(line);
		return joined ? ResponseEntity.ok(line) : ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{lineId}")
	@Operation(summary = "대기열 나가기")
	public ResponseEntity<Void> exit(@PathVariable Integer lineId) {
		boolean removed = lineService.exitLine(lineId);
		return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@GetMapping("/attraction/{attId}")
	@Operation(summary = "어트랙션별 대기열 조회")
	public ResponseEntity<List<AttractionLine>> list(@PathVariable Integer attId) {
		return ResponseEntity.ok(lineService.getLines(attId));
	}

	@PostMapping("/{lineId}/friends")
	@Operation(summary = "대기열에 친구 추가")
	public ResponseEntity<?> addFriend(@PathVariable Integer lineId, @RequestBody FriendLine friendLine) {
		friendLine.setLineId(lineId);
		boolean added = lineService.addFriendToLine(friendLine);
		return added ? ResponseEntity.ok(friendLine) : ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/friends/{friendLineId}")
	@Operation(summary = "대기열 친구 제거")
	public ResponseEntity<Void> removeFriend(@PathVariable Integer friendLineId) {
		boolean removed = lineService.removeFriendFromLine(friendLineId);
		return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@GetMapping("/{lineId}/friends")
	@Operation(summary = "대기열에 포함된 친구 목록 조회")
	public ResponseEntity<List<FriendLine>> getFriendLines(@PathVariable Integer lineId) {
		return ResponseEntity.ok(lineService.getFriendLines(lineId));
	}
}
