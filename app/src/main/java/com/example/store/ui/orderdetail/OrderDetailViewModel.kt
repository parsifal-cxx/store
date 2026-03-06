package com.example.store.ui.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.OrdersRepository
import com.example.store.data.ProductImagesRepository
import com.example.store.data.model.OrderDto
import com.example.store.data.model.OrderItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/** ViewModel детализации заказа. Дата: 06.03.2026, Автор: Бубнов Никита */
class OrderDetailViewModel : ViewModel() {

    data class UiItem(
        val title: String,
        val price: String,
        val count: String,
        val imageUrl: String?
    )

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val order: OrderDto? = null,
        val items: List<UiItem> = emptyList()
    )

    private val repo = OrdersRepository()
    private val imgRepo = ProductImagesRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    fun dismissError() { _state.value = _state.value.copy(errorRes = null) }

    fun load(orderId: Long) {
        viewModelScope.launch {
            _state.value = UiState(loading = true)

            val orderRes = repo.loadOrder(orderId)
            if (orderRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(orderRes.exceptionOrNull()))
                return@launch
            }

            val itemsRes = repo.loadOrderItems(orderId)
            if (itemsRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(itemsRes.exceptionOrNull()))
                return@launch
            }

            val items = itemsRes.getOrNull().orEmpty()
            val pids = items.mapNotNull { it.productId }.distinct()
            val preview = imgRepo.getPreviewUrlsForProducts(pids).getOrNull().orEmpty()

            _state.value = _state.value.copy(
                loading = false,
                order = orderRes.getOrNull(),
                items = items.map { it.toUi(preview[it.productId]) }
            )
        }
    }

    private fun OrderItemDto.toUi(img: String?): UiItem =
        UiItem(
            title = title.orEmpty(),
            price = "₽%.2f".format(coast ?: 0.0),
            count = "${count ?: 1} шт",
            imageUrl = img
        )

    private fun mapError(t: Throwable?): Int =
        when (t) {
            is UnknownHostException -> R.string.error_no_internet
            else -> R.string.error_unknown
        }
}