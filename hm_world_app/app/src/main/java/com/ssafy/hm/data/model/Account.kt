package com.ssafy.hm.data.model

data class Account(
    val userId: String,
    val pw: String,
    val name: String?,
    val phone: String? = null,
    val birth: String? = null,
    val attId: Int? = null,
    val ticket: Boolean = false
)
