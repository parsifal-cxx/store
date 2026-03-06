package com.example.store.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.R
import com.example.store.data.CartRepository
import com.example.store.data.OrdersRepository
import com.example.store.data.ProductsRepository
import com.example.store.data.ProfileRepository
import com.example.store.data.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/** ViewModel Checkout. Дата: 06.03.2026, Автор: Бубнов Никита */
class CheckoutViewModel : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val errorRes: Int? = null,
        val email: String = "",
        val phone: String = "",
        val address: String = "",
        val sum: String = "",
        val delivery: String = "",
        val total: String = "",
        val showDialog: Boolean = false
    )

    private val profileRepo = ProfileRepository()
    private val cartRepo = CartRepository()
    private val prodRepo = ProductsRepository()
    private val ordersRepo = OrdersRepository()

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    private val deliveryValue = 60.20

    init { load() }

    fun dismissError() { _state.value = _state.value.copy(errorRes = null) }
    fun dismissDialog() { _state.value = _state.value.copy(showDialog = false) }

    fun setEmail(v: String) { _state.value = _state.value.copy(email = v) }
    fun setPhone(v: String) { _state.value = _state.value.copy(phone = v) }
    fun setAddress(v: String) { _state.value = _state.value.copy(address = v) }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, errorRes = null)

            val userId = cartRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(loading = false, errorRes = R.string.error_not_authorized)
                return@launch
            }

            val profile = profileRepo.loadProfile(userId).getOrNull()
            val sessionEmail = profileRepo.currentUserId()?.let { null } // не используем
            _state.value = _state.value.copy(
                email = profile?.userId?.let { _state.value.email }.orEmpty().ifBlank { "" },
                phone = profile?.phone.orEmpty(),
                address = profile?.address.orEmpty()
            )

            val cart = cartRepo.loadCart(userId).getOrNull().orEmpty()
            val ids = cart.mapNotNull { it.productId }.distinct()
            val products = prodRepo.getProductsByIds(ids).getOrNull().orEmpty().associateBy { it.id }

            val pairs: List<Pair<ProductDto, Long>> = cart.mapNotNull { row ->
                val pid = row.productId ?: return@mapNotNull null
                val p = products[pid] ?: return@mapNotNull null
                p to (row.count ?: 1)
            }

            val sum = pairs.sumOf { it.first.cost * it.second }
            val total = sum + deliveryValue

            _state.value = _state.value.copy(
                loading = false,
                sum = "₽%.2f".format(sum),
                delivery = "₽%.2f".format(deliveryValue),
                total = "₽%.2f".format(total)
            )
        }
    }

    fun confirmOrder() {
        viewModelScope.launch {
            val userId = cartRepo.currentUserId()
            if (userId == null) {
                _state.value = _state.value.copy(errorRes = R.string.error_not_authorized)
                return@launch
            }

            _state.value = _state.value.copy(loading = true, errorRes = null)

            val cart = cartRepo.loadCart(userId)
            if (cart.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(cart.exceptionOrNull()))
                return@launch
            }

            val rows = cart.getOrNull().orEmpty()
            val ids = rows.mapNotNull { it.productId }.distinct()
            val products = prodRepo.getProductsByIds(ids).getOrNull().orEmpty().associateBy { it.id }

            val pairs = rows.mapNotNull { row ->
                val pid = row.productId ?: return@mapNotNull null
                val p = products[pid] ?: return@mapNotNull null
                p to (row.count ?: 1)
            }

            val orderIdRes = ordersRepo.createOrder(
                userId = userId,
                email = _state.value.email,
                phone = _state.value.phone,
                address = _state.value.address,
                delivery = 60, // поле int8, UI показывает 60.20
                products = pairs
            )

            if (orderIdRes.isFailure) {
                _state.value = _state.value.copy(loading = false, errorRes = mapError(orderIdRes.exceptionOrNull()))
                return@launch
            }

            cartRepo.clear(userId)

            _state.value = _state.value.copy(loading = false, showDialog = true)
        }
    }

    private fun mapError(t: Throwable?): Int =
        when (t) {
            is UnknownHostException -> R.string.error_no_internet
            else -> R.string.error_unknown
        }
}