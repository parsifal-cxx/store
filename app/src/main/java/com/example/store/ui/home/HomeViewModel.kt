package com.example.store.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.CartRepository
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

/** ViewModel главного экрана. Дата: 06.03.2026, Автор: Бубнов Никита */
class HomeViewModel : ViewModel() {

    data class UiCategory(val id: String?, val title: String)

    data class UiProduct(
        val id: String,
        val title: String,
        val price: String,
        val isBestSeller: Boolean,
        val imageUrl: String?,
        val isFavorite: Boolean,
        val isInCart: Boolean
    )

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val categories: List<UiCategory> = emptyList(),
        val selectedCategoryId: String? = null,
        val bestSellers: List<UiProduct> = emptyList(),
        val allProducts: List<UiProduct> = emptyList(),
        val favoriteIds: Set<String> = emptySet(),
        val cartIds: Set<String> = emptySet()
    )

    private val productsRepo = ProductsRepository()
    private val imagesRepo = ProductImagesRepository()
    private val favRepo = FavoritesRepository()
    private val cartRepo = CartRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    init { load() }

    fun dismissError() { _state.value = _state.value.copy(errorRes = null) }

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

            val currentSet = _state.value.favoriteIds.toMutableSet()
            val isFav = currentSet.contains(productId)
            if (isFav) currentSet.remove(productId) else currentSet.add(productId)

            updateProductsState(favIds = currentSet)

            val res = if (isFav) favRepo.removeFromFavorite(userId, productId) else favRepo.addToFavorite(userId, productId)
            if (res.isFailure) {
                _state.value = _state.value.copy(errorRes = mapError(res.exceptionOrNull()))
                updateProductsState(favIds = _state.value.favoriteIds) // rollback
            }
        }
    }

    fun toggleCart(productId: String) {
        viewModelScope.launch {
            val userId = cartRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(errorRes = R.string.error_not_authorized)
                return@launch
            }

            val currentSet = _state.value.cartIds.toMutableSet()
            val inCart = currentSet.contains(productId)
            if (inCart) currentSet.remove(productId) else currentSet.add(productId)

            updateProductsState(cartIds = currentSet)

            // Если удаляем - нужно найти rowId, но для простоты на Home пока только добавление
            // Или если нужно удаление: cartRepo.loadCart -> find row -> delete.
            // Для скорости: inc (добавить) работает всегда. Удаление сложнее без rowId.
            // Реализуем: если было в корзине - удаляем все вхождения.
            val res = if (inCart) {
                cartRepo.removeAll(userId, productId)
            } else {
                cartRepo.inc(userId, productId)
            }

            if (res.isFailure) {
                _state.value = _state.value.copy(errorRes = mapError(res.exceptionOrNull()))
                updateProductsState(cartIds = _state.value.cartIds) // rollback
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorRes = null)
            val catRes = productsRepo.getCategories()
            if (catRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(catRes.exceptionOrNull()))
                return@launch
            }
            val cats = buildList {
                add(UiCategory(id = null, title = "Все"))
                catRes.getOrNull().orEmpty().forEach { add(it.toUi()) }
            }
            _state.value = _state.value.copy(categories = cats)
            loadProducts()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val catId = _state.value.selectedCategoryId
            _state.value = _state.value.copy(loading = true)

            val userId = favRepo.currentUserId()
            val favIds = if (userId != null) favRepo.getFavoriteProductIds(userId).getOrNull().orEmpty().toSet() else emptySet()
            val cartIds = if (userId != null) cartRepo.getProductIdsInCart(userId).getOrNull().orEmpty().toSet() else emptySet()

            val bestRes = productsRepo.getBestSellers(catId)
            val allRes = productsRepo.getProducts(catId)

            if (allRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(allRes.exceptionOrNull()))
                return@launch
            }

            val best = bestRes.getOrNull().orEmpty()
            val all = allRes.getOrNull().orEmpty()

            val ids = (best.map { it.id } + all.map { it.id }).distinct()
            val previews = imagesRepo.getPreviewUrlsForProducts(ids).getOrNull().orEmpty()

            _state.value = _state.value.copy(
                loading = false,
                favoriteIds = favIds,
                cartIds = cartIds,
                bestSellers = best.map { it.toUi(previews[it.id], favIds.contains(it.id), cartIds.contains(it.id)) },
                allProducts = all.map { it.toUi(previews[it.id], favIds.contains(it.id), cartIds.contains(it.id)) }
            )
        }
    }

    private fun updateProductsState(favIds: Set<String>? = null, cartIds: Set<String>? = null) {
        val f = favIds ?: _state.value.favoriteIds
        val c = cartIds ?: _state.value.cartIds
        _state.value = _state.value.copy(
            favoriteIds = f,
            cartIds = c,
            bestSellers = _state.value.bestSellers.map { it.copy(isFavorite = f.contains(it.id), isInCart = c.contains(it.id)) },
            allProducts = _state.value.allProducts.map { it.copy(isFavorite = f.contains(it.id), isInCart = c.contains(it.id)) }
        )
    }

    private fun CategoryDto.toUi(): UiCategory = UiCategory(id = id, title = title)
    private fun ProductDto.toUi(img: String?, isFav: Boolean, inCart: Boolean): UiProduct {
        val priceText = String.format(Locale.US, "₽%.2f", cost)
        return UiProduct(id, title, priceText, isBestSeller == true, img, isFav, inCart)
    }

    private fun mapError(t: Throwable?): Int = if (t is UnknownHostException) R.string.error_no_internet else R.string.error_unknown
}