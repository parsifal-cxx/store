package com.example.store.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.FavoritesRepository
import com.example.store.data.ProductImagesRepository
import com.example.store.data.ProductsRepository
import com.example.store.data.model.CategoryDto
import com.example.store.data.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.Locale

/** ViewModel главного экрана. Дата: 05.03.2026, Автор: Бубнов Никита */
class HomeViewModel : ViewModel() {

    data class UiCategory(val id: String?, val title: String)

    data class UiProduct(
        val id: String,
        val title: String,
        val price: String,
        val isBestSeller: Boolean,
        val imageUrl: String?,
        val isFavorite: Boolean
    )

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val categories: List<UiCategory> = emptyList(),
        val selectedCategoryId: String? = null,
        val bestSellers: List<UiProduct> = emptyList(),
        val allProducts: List<UiProduct> = emptyList(),
        val favoriteIds: Set<String> = emptySet()
    )

    private val productsRepo = ProductsRepository()
    private val imagesRepo = ProductImagesRepository()
    private val favRepo = FavoritesRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    init { load() }

    fun dismissError() {
        _state.value = _state.value.copy(errorRes = null)
    }

    fun selectCategory(categoryId: String?) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId, loading = true, errorRes = null)
        loadProducts()
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val userId = favRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(errorRes = R.string.error_not_authorized)
                return@launch
            }

            val currentlyFav = _state.value.favoriteIds.contains(productId)
            val newSet = _state.value.favoriteIds.toMutableSet().apply {
                if (currentlyFav) remove(productId) else add(productId)
            }.toSet()

            _state.value = _state.value.copy(
                favoriteIds = newSet,
                bestSellers = _state.value.bestSellers.map { it.copy(isFavorite = newSet.contains(it.id)) },
                allProducts = _state.value.allProducts.map { it.copy(isFavorite = newSet.contains(it.id)) }
            )

            val res = if (currentlyFav) favRepo.removeFromFavorite(userId, productId) else favRepo.addToFavorite(userId, productId)
            if (res.isFailure) {
                _state.value = _state.value.copy(errorRes = mapError(res.exceptionOrNull()))
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorRes = null)

            val categoriesResult = productsRepo.getCategories()
            if (categoriesResult.isFailure) {
                Log.e("HomeViewModel", "getCategories failed", categoriesResult.exceptionOrNull())
                _state.value = _state.value.copy(loading = false, errorRes = mapError(categoriesResult.exceptionOrNull()))
                return@launch
            }

            val categories = buildList {
                add(UiCategory(id = null, title = "Все"))
                categoriesResult.getOrNull().orEmpty().forEach { add(it.toUi()) }
            }

            _state.value = _state.value.copy(categories = categories, loading = true, errorRes = null)
            loadProducts()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val categoryId = _state.value.selectedCategoryId

            val userId = favRepo.currentUserId()
            val favIds = if (userId != null) favRepo.getFavoriteProductIds(userId).getOrNull().orEmpty().toSet() else emptySet()

            val bestResult = productsRepo.getBestSellers(categoryId)
            if (bestResult.isFailure) Log.e("HomeViewModel", "getBestSellers failed", bestResult.exceptionOrNull())

            val allResult = productsRepo.getProducts(categoryId)
            if (allResult.isFailure) {
                Log.e("HomeViewModel", "getProducts failed", allResult.exceptionOrNull())
                _state.value = _state.value.copy(loading = false, errorRes = mapError(allResult.exceptionOrNull()))
                return@launch
            }

            val best = bestResult.getOrNull().orEmpty()
            val all = allResult.getOrNull().orEmpty()

            val ids = (best.asSequence().map { it.id } + all.asSequence().map { it.id }).distinct().toList()
            val previewsResult = imagesRepo.getPreviewUrlsForProducts(ids)
            if (previewsResult.isFailure) Log.e("HomeViewModel", "storage list failed", previewsResult.exceptionOrNull())
            val previewMap = previewsResult.getOrNull().orEmpty()

            _state.value = _state.value.copy(
                loading = false,
                errorRes = null,
                favoriteIds = favIds,
                bestSellers = best.map { it.toUi(previewMap[it.id], favIds.contains(it.id)) },
                allProducts = all.map { it.toUi(previewMap[it.id], favIds.contains(it.id)) }
            )
        }
    }

    private fun CategoryDto.toUi(): UiCategory = UiCategory(id = id, title = title)

    private fun ProductDto.toUi(imageUrl: String?, isFavorite: Boolean): UiProduct {
        val priceText = String.format(Locale.US, "₽%.2f", cost)
        return UiProduct(
            id = id,
            title = title,
            price = priceText,
            isBestSeller = isBestSeller == true,
            imageUrl = imageUrl,
            isFavorite = isFavorite
        )
    }

    private fun mapError(t: Throwable?): Int =
        when (t) {
            is UnknownHostException -> R.string.error_no_internet
            else -> R.string.error_unknown
        }
}