package com.ssafy.hm.recommend

import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.Item
import kotlin.math.max

data class RecommendationResult(
    val attractions: List<Attraction>,
    val items: List<Item>
)

class MiniLmRecommender(private val embedder: MiniLmEmbedder) {
    private val attractionCache = mutableMapOf<Int, FloatArray>()
    private val itemCache = mutableMapOf<Int, FloatArray>()

    fun recommend(
        preferenceText: String,
        attractions: List<Attraction>,
        items: List<Item>
    ): RecommendationResult {
        val userEmbedding = embedder.embed(preferenceText)
        val topAttractions = attractions
            .map { it to score(userEmbedding, getAttractionEmbedding(it)) }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(2)
        val topItems = items
            .map { it to score(userEmbedding, getItemEmbedding(it)) }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(2)
        return RecommendationResult(topAttractions, topItems)
    }

    private fun getAttractionEmbedding(attraction: Attraction): FloatArray {
        val cached = attractionCache[attraction.attId]
        if (cached != null) return cached
        val text = listOfNotNull(attraction.attCategory, attraction.attComment)
            .joinToString(" ")
            .ifBlank { attraction.attName ?: "" }
        val embedding = embedder.embed(text)
        attractionCache[attraction.attId] = embedding
        return embedding
    }

    private fun getItemEmbedding(item: Item): FloatArray {
        val cached = itemCache[item.itemId]
        if (cached != null) return cached
        val text = listOfNotNull(item.itemCategory, item.itemComment)
            .joinToString(" ")
            .ifBlank { item.itemName }
        val embedding = embedder.embed(text)
        itemCache[item.itemId] = embedding
        return embedding
    }

    private fun score(a: FloatArray, b: FloatArray): Float {
        var sum = 0f
        val size = max(a.size, b.size)
        val limit = minOf(a.size, b.size)
        for (i in 0 until limit) sum += a[i] * b[i]
        if (size == 0) return 0f
        return sum
    }
}
