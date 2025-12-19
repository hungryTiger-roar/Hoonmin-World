package com.ssafy.hm.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.hm.BuildConfig
import com.ssafy.hm.data.model.HomeImage
import com.ssafy.hm.data.repository.HomeRepository
import com.ssafy.hm.data.repository.UploadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

data class UploadUiState(
    val loading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

class UploadViewModel(
    private val uploadRepo: UploadRepository,
    private val homeRepo: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UploadUiState())
    val state: StateFlow<UploadUiState> = _state

    fun uploadImage(part: MultipartBody.Part) {
        viewModelScope.launch {
            _state.update { UploadUiState(loading = true) }
            runCatching {
                withContext(Dispatchers.IO) { uploadRepo.uploadImage(part) }
            }.onSuccess { message ->
                _state.update { UploadUiState(message = message) }
            }.onFailure { e ->
                _state.update { UploadUiState(error = e.message) }
            }
        }
    }

    fun uploadAndInsertHomeImage(part: MultipartBody.Part) {
        viewModelScope.launch {
            _state.update { UploadUiState(loading = true) }
            runCatching {
                withContext(Dispatchers.IO) { uploadRepo.uploadImage(part) }
            }.onSuccess {
                _state.update { UploadUiState(message = "홈 이미지 등록 완료") }
            }.onFailure { e ->
                _state.update { UploadUiState(error = e.message) }
            }
        }
    }

    fun uploadAndInsertBuyImage(part: MultipartBody.Part) {
        viewModelScope.launch {
            _state.update { UploadUiState(loading = true) }
            runCatching {
                val response = withContext(Dispatchers.IO) { uploadRepo.uploadImage(part) }
                val link = extractDownloadLink(response)
                    ?: throw IllegalStateException("업로드는 성공했지만 다운로드 링크를 찾을 수 없습니다.")
                withContext(Dispatchers.IO) { homeRepo.createBuyImage(link) }
            }.onSuccess {
                _state.update { UploadUiState(message = "상품 이미지 등록 완료") }
            }.onFailure { e ->
                _state.update { UploadUiState(error = e.message) }
            }
        }
    }

    suspend fun uploadAndGetLink(part: MultipartBody.Part): String {
        val response = withContext(Dispatchers.IO) { uploadRepo.uploadImage(part) }
        return extractDownloadLink(response)
            ?: throw IllegalStateException("업로드는 성공했지만 다운로드 링크를 찾을 수 없습니다.")
    }

    fun clearResult() {
        _state.update { it.copy(message = null, error = null) }
    }
}

private fun extractDownloadLink(raw: String): String? {
    val absoluteWithExt = Regex(
        "https?://[^\\s\"'>]+/uploaded/[^\\s\"'>]+\\.(jpg|jpeg|png|gif|webp)",
        RegexOption.IGNORE_CASE
    )
    val absoluteMatch = absoluteWithExt.find(raw)?.value
    if (!absoluteMatch.isNullOrBlank()) return absoluteMatch

    val relativeWithExt = Regex("/uploaded/[^\\s\"'>]+\\.(jpg|jpeg|png|gif|webp)", RegexOption.IGNORE_CASE)
    val relativeMatch = relativeWithExt.find(raw)?.value
    if (!relativeMatch.isNullOrBlank()) {
        val baseUrl = BuildConfig.BASE_URL.trimEnd('/')
        return baseUrl + relativeMatch
    }

    val relativeFallback = Regex("/uploaded/[^\\s\"'>]+")
    val fallbackMatch = relativeFallback.find(raw)?.value ?: return null
    val baseUrl = BuildConfig.BASE_URL.trimEnd('/')
    return baseUrl + fallbackMatch
}
