package com.example.store.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.CartRepository
import com.example.store.data.FavoritesRepository
import com.example.store.data.ProductImagesRepository
import com.example.store.data.ProductsRepository
import com.example.store.data.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.Locale

/** ViewModel деталей товара. Дата: 06.03.2026, Автор: Бубнов Никита */
class DetailsViewModel : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val product: ProductDto? = null,
        val categoryTitle: String = "",
        val price: String = "",
        val images: List<String> = emptyList(),
        val isFavorite: Boolean = false,
        val isInCart: Boolean = false
    )

    private val prodRepo = ProductsRepository()
    private val imgRepo = ProductImagesRepository()
    private val favRepo = FavoritesRepository()
    private val cartRepo = CartRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    fun dismissError() { _state.value = _state.value.copy(errorRes = null) }

    fun load(productId: String) {
        viewModelScope.launch {
            _state.value = UiState(loading = true)

            val pRes = prodRepo.getProductById(productId)
            if (pRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(pRes.exceptionOrNull()))
                return@launch
            }

            val product = pRes.getOrNull()
            if (product == null) {
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_unknown)
                return@launch
            }

            val images = imgRepo.getVariantUrls(product.id).getOrNull().orEmpty()
            val price = String.format(Locale.US, "₽%.2f", product.cost)

            val userId = favRepo.currentUserId()
            val fav = if (userId != null) favRepo.isFavorite(userId, product.id).getOrNull() == true else false
            val inCart = if (userId != null) {
                cartRepo.getProductIdsInCart(userId).getOrNull().orEmpty().contains(product.id)
            } else false

            _state.value = _state.value.copy(
                loading = false,
                product = product,
                images = images,
                price = price,
                categoryTitle = product.categoryId ?: "",
                isFavorite = fav,
                isInCart = inCart
            )
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val p = _state.value.product ?: return@launch
            val userId = favRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(errorRes = R.string.error_not_authorized)
                return@launch
            }

            val currently = _state.value.isFavorite
            _state.value = _state.value.copy(isFavorite = !currently)

            val res = if (currently) favRepo.removeFromFavorite(userId, p.id) else favRepo.addToFavorite(userId, p.id)
            if (res.isFailure) {
                _state.value = _state.value.copy(errorRes = mapError(res.exceptionOrNull()))
            }
        }
    }

    fun toggleCart() {
        viewModelScope.launch {
            val p = _state.value.product ?: return@launch
            val userId = cartRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(errorRes = R.string.error_not_authorized)
                return@launch
            }

            val currently = _state.value.isInCart
            _state.value = _state.value.copy(isInCart = !currently)

            val res = if (currently) {
                cartRepo.removeAll(userId, p.id)
            } else {
                cartRepo.inc(userId, p.id)
            }

            if (res.isFailure) {
                _state.value = _state.value.copy(errorRes = mapError(res.exceptionOrNull()))
            }
        }
    }

    private fun mapError(t: Throwable?): Int =
        when (t) {
            is UnknownHostException -> R.string.error_no_internet
            else -> R.string.error_unknown
        }
}