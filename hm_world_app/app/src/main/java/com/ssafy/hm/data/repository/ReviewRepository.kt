package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.AttractionReview
import com.ssafy.hm.data.model.ItemReview
import com.ssafy.hm.data.network.HmApi

class ReviewRepository(private val api: HmApi) {
    suspend fun getItemReviews(itemId: Int): List<ItemReview> = api.getItemReviews(itemId)
    suspend fun addItemReview(review: ItemReview) = api.addItemReview(review)
    suspend fun updateItemReview(id: Int, review: ItemReview) = api.updateItemReview(id, review)
    suspend fun deleteItemReview(id: Int) = api.deleteItemReview(id)

    suspend fun getAttractionReviews(attId: Int): List<AttractionReview> = api.getAttractionReviews(attId)
    suspend fun addAttractionReview(review: AttractionReview) = api.addAttractionReview(review)
    suspend fun updateAttractionReview(id: Int, review: AttractionReview) = api.updateAttractionReview(id, review)
    suspend fun deleteAttractionReview(id: Int) = api.deleteAttractionReview(id)
}
