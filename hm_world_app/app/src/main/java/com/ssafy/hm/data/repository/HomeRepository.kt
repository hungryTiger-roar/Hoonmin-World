package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.BuyImageRequest
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.data.network.HmApi

class HomeRepository(private val api: HmApi) {
    suspend fun getHomeImages(): List<HomeImage> = api.getHomeImages()
    suspend fun getBuyImages(): List<BuyImage> = api.getBuyImages()
    suspend fun createHomeImage(image: HomeImage): HomeImage = api.createHomeImage(image)
    suspend fun deleteHomeImage(homeId: Int) = api.deleteHomeImage(homeId)
    suspend fun createBuyImage(url: String): BuyImage = api.createBuyImage(BuyImageRequest(url))
    suspend fun deleteBuyImage(buyId: Int) = api.deleteBuyImage(buyId)
}
