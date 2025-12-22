package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.Account
import com.ssafy.hm.data.network.HmApi

class AccountRepository(private val api: HmApi) {
    suspend fun login(id: String, pw: String): Account = api.login(mapOf("id" to id, "pw" to pw))
    suspend fun register(account: Account): Account = api.register(account)
    suspend fun getAccount(userId: String): Account = api.getAccount(userId)
    suspend fun getAccounts(): List<Account> = api.getAccounts()
}
