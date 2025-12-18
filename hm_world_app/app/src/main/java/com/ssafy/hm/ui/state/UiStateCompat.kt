package com.ssafy.hm.ui.state

import com.ssafy.hm.data.model.Attraction
import com.ssafy.hm.data.model.BuyImage
import com.ssafy.hm.data.model.HomeBoard
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.data.model.Item

/**
 * 호환용 UI 상태 모델: 관리 화면 등에서 간단히 집계 표시를 위해 사용.
 */
data class HomeData(
    val homeImages: List<HomeImage> = emptyList(),
    val buyImages: List<BuyImage> = emptyList(),
    val boards: List<HomeBoard> = emptyList(),
    val items: List<Item> = emptyList(),
    val attractions: List<Attraction> = emptyList()
)

data class MainUiState(
    val data: HomeData = HomeData()
)
