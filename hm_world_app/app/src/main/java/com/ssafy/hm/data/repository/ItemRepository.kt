package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.network.HmApi
import okhttp3.MultipartBody

class ItemRepository(private val api: HmApi) {
    suspend fun getItems(): List<Item> = api.getItems()
    suspend fun getBuyImages(): List<BuyImage> = api.getBuyImages()
    suspend fun getItem(id: Int): Item = api.getItem(id)
    suspend fun createItem(item: Item): Item = api.createItem(item)
    suspend fun updateItem(item: Item): Item = api.updateItem(item.itemId, item)
    suspend fun deleteItem(id: Int) = api.deleteItem(id)
    suspend fun classifyImageCategory(part: MultipartBody.Part) = api.classifyImageCategory(part)
}
