package com.example.store.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.CartRepository
import com.example.store.data.ProductImagesRepository
import com.example.store.data.ProductsRepository
import com.example.store.data.model.CartRowDto
import com.example.store.data.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.Locale

/** ViewModel корзины. Дата: 06.03.2026, Автор: Бубнов Никита */
class CartViewModel : ViewModel() {

    data class UiItem(
        val rowId: String,
        val productId: String,
        val title: String,
        val price: String,
        val count: Long,
        val imageUrl: String?
    )

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val items: List<UiItem> = emptyList(),
        val sum: String = "",
        val delivery: String = "",
        val total: String = ""
    )

    private val cartRepo = CartRepository()
    private val prodRepo = ProductsRepository()
    private val imgRepo = ProductImagesRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    private val deliveryValue = 60.20

    init { load() }

    fun dismissError() { _state.value = _state.value.copy(errorRes = null) }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorRes = null)

            val userId = cartRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_not_authorized)
                return@launch
            }

            val cart = cartRepo.loadCart(userId)
            if (cart.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(cart.exceptionOrNull()))
                return@launch
            }

            val rows = cart.getOrNull().orEmpty()
            val ids = rows.mapNotNull { it.productId }.distinct()

            val products = prodRepo.getProductsByIds(ids).getOrNull().orEmpty().associateBy { it.id }
            val preview = imgRepo.getPreviewUrlsForProducts(ids).getOrNull().orEmpty()

            val ui = rows.mapNotNull { row ->
                val pid = row.productId ?: return@mapNotNull null
                val p = products[pid] ?: return@mapNotNull null
                row.toUi(p, preview[pid])
            }

            val sumValue = ui.sumOf { parsePrice(it.price) * it.count }
            val totalValue = sumValue + deliveryValue

            _state.value = _state.value.copy(
                loading = false,
                items = ui,
                sum = money(sumValue),
                delivery = money(deliveryValue),
                total = money(totalValue)
            )
        }
    }

    fun inc(productId: String) {
        viewModelScope.launch {
            val userId = cartRepo.currentUserId() ?: return@launch
            cartRepo.inc(userId, productId)
            load()
        }
    }

    fun dec(productId: String) {
        viewModelScope.launch {
            val userId = cartRepo.currentUserId() ?: return@launch
            cartRepo.dec(userId, productId)
            load()
        }
    }

    fun deleteRow(rowId: String) {
        viewModelScope.launch {
            cartRepo.deleteRow(rowId)
            load()
        }
    }

    private fun CartRowDto.toUi(p: ProductDto, imageUrl: String?): UiItem {
        val c = (count ?: 1).coerceAtLeast(1)
        return UiItem(
            rowId = id,
            productId = p.id,
            title = p.title,
            price = money(p.cost),
            count = c,
            imageUrl = imageUrl
        )
    }

    private fun money(v: Double): String = String.format(Locale.US, "₽%.2f", v)

    private fun parsePrice(s: String): Double =
        s.replace("₽", "").trim().replace(",", ".").toDoubleOrNull() ?: 0.0

    private fun mapError(t: Throwable?): Int =
        when (t) {
            is UnknownHostException -> R.string.error_no_internet
            else -> R.string.error_unknown
        }
}