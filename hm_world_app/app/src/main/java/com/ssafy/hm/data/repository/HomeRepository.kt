package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.data.network.HmApi

class HomeRepository(private val api: HmApi) {
    suspend fun getHomeImages(): List<HomeImage> = api.getHomeImages()
    suspend fun getBuyImages(): List<BuyImage> = api.getBuyImages()
}
