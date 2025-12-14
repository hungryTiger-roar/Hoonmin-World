package com.ssafy.hm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.ssafy.hm.dto.Account;
import com.ssafy.hm.service.AccountService;

@RestController
@RequestMapping("/accounts")
@Tag(name = "계정", description = "회원 관리 및 로그인")
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping
	@Operation(summary = "전체 회원 조회")
	public ResponseEntity<List<Account>> getAll() {
		return ResponseEntity.ok(accountService.getAll());
	}

	@GetMapping("/{userId}")
	@Operation(summary = "회원 단건 조회")
	public ResponseEntity<Account> get(@PathVariable String userId) {
		Account account = accountService.get(userId);
		return account != null ? ResponseEntity.ok(account) : ResponseEntity.notFound().build();
	}

	@PostMapping
	@Operation(summary = "회원 등록")
	public ResponseEntity<?> register(@RequestBody Account account) {
		boolean created = accountService.register(account);
		return created ? ResponseEntity.ok(account) : ResponseEntity.badRequest().build();
	}

	@PutMapping("/{userId}")
	@Operation(summary = "회원 수정")
	public ResponseEntity<?> update(@PathVariable String userId, @RequestBody Account account) {
		account.setUserId(userId);
		boolean updated = accountService.update(account);
		return updated ? ResponseEntity.ok(account) : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{userId}")
	@Operation(summary = "회원 삭제")
	public ResponseEntity<Void> delete(@PathVariable String userId) {
		boolean removed = accountService.remove(userId);
		return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@PostMapping("/login")
	@Operation(summary = "로그인")
	public ResponseEntity<Account> login(@RequestBody Map<String, String> payload) {
		Map<String, String> params = new HashMap<>();
		params.put("id", payload.get("id"));
		params.put("pw", payload.get("pw"));
		Account account = accountService.login(params);
		return account != null ? ResponseEntity.ok(account) : ResponseEntity.status(401).build();
	}
}
