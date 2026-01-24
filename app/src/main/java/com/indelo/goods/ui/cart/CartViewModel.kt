package com.indelo.goods.ui.cart

import androidx.lifecycle.ViewModel
import com.indelo.goods.data.model.Cart
import com.indelo.goods.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    fun addToCart(product: Product, quantity: Int = 1) {
        _cart.update { it.addItem(product, quantity) }
    }

    fun removeFromCart(productId: String) {
        _cart.update { it.removeItem(productId) }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        _cart.update { it.updateQuantity(productId, quantity) }
    }

    fun clearCart() {
        _cart.update { it.clear() }
    }
}
