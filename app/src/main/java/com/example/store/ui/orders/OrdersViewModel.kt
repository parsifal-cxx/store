package com.example.store.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.CartRepository
import com.example.store.data.OrdersRepository
import com.example.store.data.ProductImagesRepository
import com.example.store.data.ProductsRepository
import com.example.store.data.model.OrderDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

/** ViewModel заказов. Дата: 06.03.2026, Автор: Бубнов Никита */
class OrdersViewModel : ViewModel() {

    sealed class UiRow {
        data class Header(val title: String) : UiRow()
        data class OrderRow(
            val orderId: Long,
            val title: String,
            val timeText: String,
            val price: String,
            val delivery: String,
            val imageUrl: String?
        ) : UiRow()
    }

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val rows: List<UiRow> = emptyList()
    )

    private val ordersRepo = OrdersRepository()
    private val imgRepo = ProductImagesRepository()
    private val cartRepo = CartRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    init { load() }

    fun dismissError() { _state.value = _state.value.copy(errorRes = null) }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorRes = null)

            val userId = ordersRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_not_authorized)
                return@launch
            }

            val ordersRes = ordersRepo.loadOrders(userId)
            if (ordersRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(ordersRes.exceptionOrNull()))
                return@launch
            }

            val orders = ordersRes.getOrNull().orEmpty()

            val rows = mutableListOf<UiRow>()
            val now = LocalDateTime.now()
            val today = now.toLocalDate()
            val yesterday = today.minusDays(1)

            val grouped = orders.groupBy { parseDate(it.createdAt).toLocalDate() }.toSortedMap(compareByDescending { it })

            for ((date, list) in grouped) {
                val header = when (date) {
                    today -> "Недавний"
                    yesterday -> "Вчера"
                    else -> date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru")))
                }
                rows += UiRow.Header(header)

                for (o in list) {
                    val items = ordersRepo.loadOrderItems(o.id).getOrNull().orEmpty()
                    val first = items.firstOrNull()
                    val title = first?.title ?: "—"

                    val timeText = if (date == today) {
                        val minutes = Duration.between(parseDate(o.createdAt), now).toMinutes().coerceAtLeast(0)
                        "$minutes мин назад"
                    } else {
                        parseDate(o.createdAt).format(DateTimeFormatter.ofPattern("HH:mm"))
                    }

                    val price = "₽%.2f".format(items.sumOf { (it.coast ?: 0.0) * (it.count ?: 1) })
                    val delivery = "₽60.20"

                    val pid = first?.productId
                    val img = if (pid != null) imgRepo.getPreviewUrlsForProducts(listOf(pid)).getOrNull()?.get(pid) else null

                    rows += UiRow.OrderRow(
                        orderId = o.id,
                        title = title,
                        timeText = timeText,
                        price = price,
                        delivery = delivery,
                        imageUrl = img
                    )
                }
            }

            _state.value = _state.value.copy(loading = false, rows = rows)
        }
    }

    fun repeatOrder(orderId: Long) {
        viewModelScope.launch {
            val userId = cartRepo.currentUserId() ?: return@launch
            val items = ordersRepo.loadOrderItems(orderId).getOrNull().orEmpty()
            for (it in items) {
                val pid = it.productId ?: continue
                val cnt = (it.count ?: 1).toInt()
                repeat(cnt) { cartRepo.inc(userId, pid) }
            }
        }
    }

    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            val res = ordersRepo.deleteOrder(orderId)
            if (res.isSuccess) load() else _state.value = _state.value.copy(errorRes = mapError(res.exceptionOrNull()))
        }
    }

    private fun parseDate(createdAt: String): LocalDateTime {
        return try {
            OffsetDateTime.parse(createdAt).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        } catch (_: Exception) {
            LocalDateTime.now()
        }
    }

    private fun mapError(t: Throwable?): Int =
        when (t) {
            is UnknownHostException -> R.string.error_no_internet
            else -> R.string.error_unknown
        }
}