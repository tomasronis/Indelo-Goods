package com.indelo.goods.data.model

data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val subtotal: Double
        get() = (product.retailPrice ?: product.wholesalePrice) * quantity
}

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    val total: Double
        get() = items.sumOf { it.subtotal }

    val itemCount: Int
        get() = items.sumOf { it.quantity }

    fun addItem(product: Product, quantity: Int = 1): Cart {
        val existingItem = items.find { it.product.id == product.id }
        return if (existingItem != null) {
            // Update quantity if item already exists
            val updatedItems = items.map {
                if (it.product.id == product.id) {
                    it.copy(quantity = it.quantity + quantity)
                } else {
                    it
                }
            }
            copy(items = updatedItems)
        } else {
            // Add new item
            copy(items = items + CartItem(product, quantity))
        }
    }

    fun removeItem(productId: String): Cart {
        return copy(items = items.filter { it.product.id != productId })
    }

    fun updateQuantity(productId: String, quantity: Int): Cart {
        val updatedItems = items.map {
            if (it.product.id == productId) {
                it.copy(quantity = quantity.coerceAtLeast(1))
            } else {
                it
            }
        }
        return copy(items = updatedItems)
    }

    fun clear(): Cart {
        return Cart(emptyList())
    }
}
