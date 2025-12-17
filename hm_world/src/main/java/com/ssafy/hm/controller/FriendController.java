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

import com.ssafy.hm.dto.Friend;
import com.ssafy.hm.service.FriendService;

@RestController
@RequestMapping("/friends")
@Tag(name = "친구", description = "친구 관리")
public class FriendController {

	private final FriendService friendService;

	public FriendController(FriendService friendService) {
		this.friendService = friendService;
	}

	@PostMapping
	@Operation(summary = "친구 추가")
	public ResponseEntity<?> add(@RequestBody Friend friend) {
		boolean added = friendService.addFriend(friend);
		return added ? ResponseEntity.ok(friend) : ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "친구 삭제")
	public ResponseEntity<Void> remove(@PathVariable Integer id) {
		boolean removed = friendService.removeFriend(id);
		return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@GetMapping("/user/{userId}")
	@Operation(summary = "사용자별 친구 목록 조회")
	public ResponseEntity<List<Friend>> list(@PathVariable String userId) {
		return ResponseEntity.ok(friendService.getFriends(userId));
	}
	

	@GetMapping("/user/{userId}/ticket")
	@Operation(summary = "사용자별 티켓 구매 친구 목록 조회")
	public ResponseEntity<List<Friend>> listWithTicket(@PathVariable String userId) {
		return ResponseEntity.ok(friendService.getFriendsTicket(userId));
	}

	@GetMapping("/user/{userId}/ticket/available")
	@Operation(summary = "사용자별 티켓 구매, 어트랙션 예약 안한 친구 목록 조회")
	public ResponseEntity<List<Friend>> listWithTicketNoAtt(@PathVariable String userId) {
		return ResponseEntity.ok(friendService.getFriendsTicketNoAtt(userId));
	}
}
