package com.ssafy.hm.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.Account;
import com.ssafy.hm.repo.AccountRepo;

@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepo accountRepo;

	public AccountServiceImpl(AccountRepo accountRepo) {
		this.accountRepo = accountRepo;
	}

	@Override
	@Transactional
	public boolean register(Account account) {
		return accountRepo.insert(account) == 1;
	}

	@Override
	@Transactional
	public boolean update(Account account) {
		return accountRepo.update(account) == 1;
	}

	@Override
	@Transactional
	public boolean remove(String userId) {
		return accountRepo.delete(userId) == 1;
	}

	@Override
	public Account get(String userId) {
		return accountRepo.selectById(userId);
	}

	@Override
	public List<Account> getAll() {
		return accountRepo.selectAll();
	}

	@Override
	public Account login(Map<String, String> params) {
		return accountRepo.login(params);
	}
}
