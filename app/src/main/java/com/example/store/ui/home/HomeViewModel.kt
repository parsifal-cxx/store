package com.example.store.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.ProductImagesRepository
import com.example.store.data.ProductsRepository
import com.example.store.data.model.CategoryDto
import com.example.store.data.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

/** ViewModel главного экрана. Дата: 05.03.2026, Автор: Бубнов Никита */
class HomeViewModel : ViewModel() {

    data class UiCategory(
        val id: String?,
        val title: String
    )

    data class UiProduct(
        val id: String,
        val title: String,
        val price: String,
        val isBestSeller: Boolean,
        val imageUrl: String?
    )

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val categories: List<UiCategory> = emptyList(),
        val selectedCategoryId: String? = null,
        val products: List<UiProduct> = emptyList()
    )

    private val productsRepo = ProductsRepository()
    private val imagesRepo = ProductImagesRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    init {
        load()
    }

    fun dismissError() {
        _state.value = _state.value.copy(errorRes = null)
    }

    fun retry() {
        load()
    }

    fun selectCategory(categoryId: String?) {
        _state.value = _state.value.copy(
            selectedCategoryId = categoryId,
            loading = true,
            errorRes = null
        )
        loadProducts()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorRes = null)

            val categoriesResult = productsRepo.getCategories()
            if (categoriesResult.isFailure) {
                Log.e("HomeViewModel", "getCategories failed", categoriesResult.exceptionOrNull())
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_unknown)
                return@launch
            }

            val categories = buildList {
                add(UiCategory(id = null, title = "Все"))
                categoriesResult.getOrNull().orEmpty().forEach { add(it.toUi()) }
            }

            _state.value = _state.value.copy(
                categories = categories,
                loading = true,
                errorRes = null
            )

            loadProducts()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val categoryId = _state.value.selectedCategoryId

            val productsResult = productsRepo.getBestSellers(categoryId)
            if (productsResult.isFailure) {
                Log.e("HomeViewModel", "getBestSellers failed", productsResult.exceptionOrNull())
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_unknown)
                return@launch
            }

            val products = productsResult.getOrNull().orEmpty()

            val previewsResult = imagesRepo.getPreviewUrlsForProducts(products.map { it.id })
            if (previewsResult.isFailure) {
                Log.e("HomeViewModel", "storage list failed", previewsResult.exceptionOrNull())
            }
            val previewMap = previewsResult.getOrNull().orEmpty()

            val uiProducts = products.map { p ->
                p.toUi(imageUrl = previewMap[p.id])
            }

            _state.value = _state.value.copy(
                loading = false,
                errorRes = null,
                products = uiProducts
            )
        }
    }

    private fun CategoryDto.toUi(): UiCategory = UiCategory(id = id, title = title)

    private fun ProductDto.toUi(imageUrl: String?): UiProduct {
        val priceText = String.format(Locale.US, "₽%.2f", cost)
        return UiProduct(
            id = id,
            title = title,
            price = priceText,
            isBestSeller = isBestSeller == true,
            imageUrl = imageUrl
        )
    }
}