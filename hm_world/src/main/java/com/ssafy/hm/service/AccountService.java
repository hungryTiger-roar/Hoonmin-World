package com.ssafy.hm.service;

import java.util.List;
import java.util.Map;

import com.ssafy.hm.dto.Account;

public interface AccountService {
	boolean register(Account account);
	boolean update(Account account);
	boolean remove(String userId);

	Account get(String userId);
	List<Account> getAll();
	Account login(Map<String, String> params);
}
