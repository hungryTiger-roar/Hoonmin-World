package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.AttractionReview
import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.ItemReview
import com.ssafy.hm.data.repository.AttractionRepository
import com.ssafy.hm.data.repository.ItemRepository
import com.ssafy.hm.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import okhttp3.MultipartBody

data class CatalogState(
    val loading: Boolean = false,
    val error: String? = null,
    val attractions: List<Attraction> = emptyList(),
    val items: List<Item> = emptyList(),
    val buyImages: List<BuyImage> = emptyList(),
    val selectedAttraction: Attraction? = null,
    val selectedItem: Item? = null,
    val attractionReviews: List<AttractionReview> = emptyList(),
    val itemReviews: List<ItemReview> = emptyList(),
    val aiLoading: Boolean = false,
    val aiCategory: String? = null,
    val aiCategoryItems: List<Item> = emptyList()
)

class CatalogViewModel(
    private val attractionRepo: AttractionRepository,
    private val itemRepo: ItemRepository,
    private val reviewRepo: ReviewRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogState())
    val state: StateFlow<CatalogState> = _state

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching {
                val attractions = attractionRepo.getAttractions()
                val items = itemRepo.getItems()
                val buyImages = itemRepo.getBuyImages()
                Triple(attractions, items, buyImages)
            }.onSuccess { (attractions, items, buyImages) ->
                _state.update {
                    it.copy(
                        loading = false,
                        attractions = attractions,
                        items = items,
                        buyImages = buyImages
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun loadAttractionDetail(id: Int) {
        viewModelScope.launch {
            runCatching {
                val att = attractionRepo.getAttraction(id)
                val reviews = reviewRepo.getAttractionReviews(id)
                Pair(att, reviews)
            }.onSuccess { (att, reviews) ->
                _state.update { it.copy(selectedAttraction = att, attractionReviews = reviews) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadItemDetail(id: Int) {
        viewModelScope.launch {
            runCatching {
                val item = itemRepo.getItem(id)
                val reviews = reviewRepo.getItemReviews(id)
                Pair(item, reviews)
            }.onSuccess { (item, reviews) ->
                _state.update { it.copy(selectedItem = item, itemReviews = reviews) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun classifyImageCategory(part: MultipartBody.Part) {
        viewModelScope.launch {
            _state.update { it.copy(aiLoading = true, error = null) }
            runCatching {
                val response = itemRepo.classifyImageCategory(part)
                val category = normalizeCategory(response.category)
                val candidates = _state.value.items.filter { item ->
                    val cat = item.itemCategory ?: ""
                    val alias = listOf(category, category.replace("악세", "액세"))
                    alias.any { aliasValue -> aliasValue.isNotBlank() && cat.contains(aliasValue, ignoreCase = true) }
                }
                Pair(category, candidates)
            }.onSuccess { (category, matches) ->
                _state.update {
                    it.copy(aiLoading = false, aiCategory = category, aiCategoryItems = matches)
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(aiLoading = false, aiCategory = null, aiCategoryItems = emptyList(), error = e.message)
                }
            }
        }
    }

    fun clearAiCategory() {
        _state.update { it.copy(aiCategory = null, aiCategoryItems = emptyList()) }
    }

    private fun normalizeCategory(raw: String?): String {
        val cleaned = raw?.replace("[^\\uAC00-\\uD7A3]".toRegex(), "")?.trim() ?: ""
        if (cleaned.isBlank()) {
            return "악세사리"
        }
        return when (cleaned) {
            "액세서리" -> "악세사리"
            else -> cleaned
        }
    }

    fun clearSelection() {
        _state.update {
            it.copy(
                selectedAttraction = null,
                selectedItem = null,
                attractionReviews = emptyList(),
                itemReviews = emptyList()
            )
        }
    }

    fun createAttraction(attraction: Attraction) {
        viewModelScope.launch {
            runCatching { attractionRepo.createAttraction(attraction) }
                .onSuccess { created ->
                    _state.update { it.copy(attractions = it.attractions + created) }
                    refresh()
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun updateAttraction(attId: Int, attraction: Attraction) {
        viewModelScope.launch {
            runCatching { attractionRepo.updateAttraction(attId, attraction) }
                .onSuccess { updated ->
                    _state.update {
                        it.copy(attractions = it.attractions.map { att ->
                            if (att.attId == attId) updated else att
                        })
                    }
                    refresh()
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun deleteAttraction(attId: Int) {
        viewModelScope.launch {
            val prev = _state.value
            _state.update { it.copy(attractions = it.attractions.filterNot { att -> att.attId == attId }) }
            runCatching { attractionRepo.deleteAttraction(attId) }
                .onSuccess { refresh() }
                .onFailure { e ->
                    _state.value = prev.copy(error = e.message)
                    refresh()
                }
        }
    }

    fun toggleAttractionAble(attId: Int) {
        viewModelScope.launch {
            val prev = _state.value
            _state.update {
                it.copy(attractions = it.attractions.map { att ->
                    if (att.attId == attId) att.copy(attAble = !att.attAble) else att
                })
            }
            runCatching { attractionRepo.toggleAttractionAble(attId) }
                .onSuccess { refresh() }
                .onFailure { e ->
                    _state.value = prev.copy(error = e.message)
                    refresh()
                }
        }
    }

    fun createItem(item: Item) {
        viewModelScope.launch {
            runCatching { itemRepo.createItem(item) }
                .onSuccess { created ->
                    _state.update { it.copy(items = it.items + created) }
                    refresh()
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun updateItem(itemId: Int, item: Item) {
        viewModelScope.launch {
            runCatching { itemRepo.updateItem(item.copy(itemId = itemId)) }
                .onSuccess { updated ->
                    _state.update {
                        it.copy(items = it.items.map { it2 -> if (it2.itemId == itemId) updated else it2 })
                    }
                    refresh()
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            val prev = _state.value
            _state.update { it.copy(items = it.items.filterNot { it.itemId == itemId }) }
            runCatching { itemRepo.deleteItem(itemId) }
                .onSuccess { refresh() }
                .onFailure { e ->
                    _state.value = prev.copy(error = e.message)
                    refresh()
                }
        }
    }

    fun addItemReview(
        itemId: Int,
        userId: String?,
        rating: Float,
        comment: String?
    ) {
        if (userId.isNullOrBlank()) {
            _state.update { it.copy(error = "리뷰 작성에는 로그인된 계정 정보가 필요합니다.") }
            return
        }
        viewModelScope.launch {
            runCatching {
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val review = ItemReview(
                    itemReviewId = 0,
                    userId = userId,
                    itemId = itemId,
                    itemReviewComment = comment,
                    itemRating = rating,
                    itemTime = now
                )
                reviewRepo.addItemReview(review)
                reviewRepo.getItemReviews(itemId)
            }.onSuccess { reviews ->
                _state.update { state ->
                    if (state.selectedItem?.itemId == itemId) {
                        state.copy(itemReviews = reviews)
                    } else {
                        state
                    }
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun addAttractionReview(
        attId: Int,
        userId: String?,
        rating: Float,
        comment: String?
    ) {
        if (userId.isNullOrBlank()) {
            _state.update { it.copy(error = "리뷰 작성에는 로그인된 계정 정보가 필요합니다.") }
            _state.update { it.copy(error = "리뷰 수정에는 로그인된 계정 정보가 필요합니다.") }
        }
        viewModelScope.launch {
            runCatching {
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val review = AttractionReview(
                    attReviewId = 0,
                    attId = attId,
                    userId = userId,
                    attReviewComment = comment,
                    attRating = rating,
                    attTime = now
                )
                reviewRepo.addAttractionReview(review)
                reviewRepo.getAttractionReviews(attId)
            }.onSuccess { reviews ->
                _state.update { state ->
                    if (state.selectedAttraction?.attId == attId) {
                        state.copy(attractionReviews = reviews)
                    } else {
                        state
                    }
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateItemReview(
        itemReviewId: Int,
        itemId: Int,
        userId: String?,
        rating: Float,
        comment: String?
    ) {
        if (userId.isNullOrBlank()) {
            _state.update { it.copy(error = "리뷰 수정에는 로그인된 계정 정보가 필요합니다.") }
            return
        }
        viewModelScope.launch {
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            _state.update { state ->
                val updated = state.itemReviews.map { review ->
                    if (review.itemReviewId == itemReviewId) {
                        review.copy(
                            itemRating = rating,
                            itemReviewComment = comment,
                            itemTime = now
                        )
                    } else {
                        review
                    }
                }
                state.copy(itemReviews = updated)
            }
            runCatching {
                val review = ItemReview(
                    itemReviewId = itemReviewId,
                    userId = userId,
                    itemId = itemId,
                    itemReviewComment = comment,
                    itemRating = rating,
                    itemTime = now
                )
                reviewRepo.updateItemReview(itemReviewId, review)
                reviewRepo.getItemReviews(itemId)
            }.onSuccess { reviews ->
                _state.update { it.copy(itemReviews = reviews) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteItemReview(itemReviewId: Int, itemId: Int) {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(itemReviews = state.itemReviews.filterNot { it.itemReviewId == itemReviewId })
            }
            runCatching {
                reviewRepo.deleteItemReview(itemReviewId)
                reviewRepo.getItemReviews(itemId)
            }.onSuccess { reviews ->
                _state.update { it.copy(itemReviews = reviews) }
            }.onFailure { e ->
                val msg = e.message
                if (msg != null && msg.startsWith("Response")) return@onFailure
                _state.update { it.copy(error = msg) }
            }
        }
    }

    fun updateAttractionReview(
        attReviewId: Int,
        attId: Int,
        userId: String?,
        rating: Float,
        comment: String?
    ) {
        if (userId.isNullOrBlank()) {
            _state.update { it.copy(error = "리뷰 수정에는 로그인된 계정 정보가 필요합니다.") }
            return
        }
        viewModelScope.launch {
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            _state.update { state ->
                val updated = state.attractionReviews.map { review ->
                    if (review.attReviewId == attReviewId) {
                        review.copy(
                            attRating = rating,
                            attReviewComment = comment,
                            attTime = now
                        )
                    } else {
                        review
                    }
                }
                state.copy(attractionReviews = updated)
            }
            runCatching {
                val review = AttractionReview(
                    attReviewId = attReviewId,
                    attId = attId,
                    userId = userId,
                    attReviewComment = comment,
                    attRating = rating,
                    attTime = now
                )
                reviewRepo.updateAttractionReview(attReviewId, review)
                reviewRepo.getAttractionReviews(attId)
            }.onSuccess { reviews ->
                _state.update { it.copy(attractionReviews = reviews) }
            }.onFailure { e ->
                val msg = e.message
                if (msg != null && msg.startsWith("Response")) return@onFailure
                _state.update { it.copy(error = msg) }
            }
        }
    }

    fun deleteAttractionReview(attReviewId: Int, attId: Int) {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(attractionReviews = state.attractionReviews.filterNot { it.attReviewId == attReviewId })
            }
            runCatching {
                reviewRepo.deleteAttractionReview(attReviewId)
                reviewRepo.getAttractionReviews(attId)
            }.onSuccess { reviews ->
                _state.update { it.copy(attractionReviews = reviews) }
            }.onFailure { e ->
                val msg = e.message
                if (msg != null && msg.startsWith("Response")) return@onFailure
                _state.update { it.copy(error = msg) }
            }
        }
    }
}
