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
import kotlinx.coroutines.launch

data class CatalogState(
    val loading: Boolean = false,
    val error: String? = null,
    val attractions: List<Attraction> = emptyList(),
    val items: List<Item> = emptyList(),
    val buyImages: List<BuyImage> = emptyList(),
    val selectedAttraction: Attraction? = null,
    val selectedItem: Item? = null,
    val attractionReviews: List<AttractionReview> = emptyList(),
    val itemReviews: List<ItemReview> = emptyList()
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
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val attractions = attractionRepo.getAttractions()
                val items = itemRepo.getItems()
                val buyImages = itemRepo.getBuyImages()
                _state.value = _state.value.copy(
                    loading = false,
                    attractions = attractions,
                    items = items,
                    buyImages = buyImages
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun loadAttractionDetail(id: Int) {
        viewModelScope.launch {
            try {
                val att = attractionRepo.getAttraction(id)
                val reviews = reviewRepo.getAttractionReviews(id)
                _state.value = _state.value.copy(selectedAttraction = att, attractionReviews = reviews)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun loadItemDetail(id: Int) {
        viewModelScope.launch {
            try {
                val item = itemRepo.getItem(id)
                val reviews = reviewRepo.getItemReviews(id)
                _state.value = _state.value.copy(selectedItem = item, itemReviews = reviews)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun clearSelection() {
        _state.value = _state.value.copy(
            selectedAttraction = null,
            selectedItem = null,
            attractionReviews = emptyList(),
            itemReviews = emptyList()
        )
    }
}
