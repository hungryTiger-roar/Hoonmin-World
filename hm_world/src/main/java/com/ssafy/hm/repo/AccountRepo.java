package com.ssafy.hm.repo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.hm.dto.Account;

@Mapper
public interface AccountRepo {
	int insert(Account account);
	int update(Account account);
	int delete(String userId);

	Account selectById(String userId);
	List<Account> selectAll();
	Account login(Map<String, String> params);
}
