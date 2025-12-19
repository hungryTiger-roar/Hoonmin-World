package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.AttractionReview
import com.ssafy.hm.data.model.Item
import com.ssafy.hm.data.model.ItemReview
import com.ssafy.hm.data.repository.AttractionRepository
import com.ssafy.hm.data.repository.ItemRepository
import com.ssafy.hm.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogState(
    val loading: Boolean = false,
    val error: String? = null,
    val attractions: List<Attraction> = emptyList(),
    val items: List<Item> = emptyList(),
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
            _state.update { it.copy(loading = true, error = null) }
            runCatching {
                val attractions = attractionRepo.getAttractions()
                val items = itemRepo.getItems()
                Pair(attractions, items)
            }.onSuccess { (attractions, items) ->
                _state.update { it.copy(loading = false, attractions = attractions, items = items) }
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
}
