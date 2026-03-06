package com.example.store.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.FavoritesRepository
import com.example.store.data.ProductImagesRepository
import com.example.store.data.ProductsRepository
import com.example.store.ui.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.Locale

/** ViewModel избранного. Дата: 05.03.2026, Автор: Бубнов Никита */
class FavoriteViewModel : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val products: List<HomeViewModel.UiProduct> = emptyList()
    )

    private val favRepo = FavoritesRepository()
    private val prodRepo = ProductsRepository()
    private val imgRepo = ProductImagesRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    init { load() }

    fun dismissError() { _state.value = _state.value.copy(errorRes = null) }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorRes = null)

            val userId = favRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_not_authorized)
                return@launch
            }

            val idsRes = favRepo.getFavoriteProductIds(userId)
            if (idsRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(idsRes.exceptionOrNull()))
                return@launch
            }

            val ids = idsRes.getOrNull().orEmpty()
            val productsRes = prodRepo.getProductsByIds(ids)
            if (productsRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(productsRes.exceptionOrNull()))
                return@launch
            }

            val products = productsRes.getOrNull().orEmpty()
            val previewMap = imgRepo.getPreviewUrlsForProducts(products.map { it.id }).getOrNull().orEmpty()

            val ui = products.map { p ->
                HomeViewModel.UiProduct(
                    id = p.id,
                    title = p.title,
                    price = String.format(Locale.US, "₽%.2f", p.cost),
                    isBestSeller = p.isBestSeller == true,
                    imageUrl = previewMap[p.id],
                    isFavorite = true
                )
            }

            _state.value = _state.value.copy(loading = false, products = ui)
        }
    }

    fun removeFromFavorite(productId: String) {
        viewModelScope.launch {
            val userId = favRepo.currentUserId() ?: return@launch
            favRepo.removeFromFavorite(userId, productId)
            load()
        }
    }

    private fun mapError(t: Throwable?): Int =
        when (t) {
            is UnknownHostException -> R.string.error_no_internet
            else -> R.string.error_unknown
        }
}