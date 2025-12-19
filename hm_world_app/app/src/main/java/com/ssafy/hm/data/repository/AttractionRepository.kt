package com.ssafy.hm.data.repository

import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.network.HmApi

class AttractionRepository(private val api: HmApi) {
    suspend fun getAttractions(): List<Attraction> = api.getAttractions()
    suspend fun getAttraction(id: Int): Attraction = api.getAttraction(id)
    suspend fun createAttraction(attraction: Attraction): Attraction = api.createAttraction(attraction)
    suspend fun updateAttraction(id: Int, attraction: Attraction): Attraction = api.updateAttraction(id, attraction)
    suspend fun toggleAttractionAble(id: Int) = api.toggleAttractionAble(id)
    suspend fun deleteAttraction(id: Int) = api.deleteAttraction(id)
}
