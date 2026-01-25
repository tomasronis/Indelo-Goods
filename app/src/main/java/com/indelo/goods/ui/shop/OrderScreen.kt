package com.indelo.goods.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    shopId: String,
    onNavigateBack: () -> Unit,
    onOrderPlaced: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(shopId) {
        viewModel.setShopId(shopId)
    }

    LaunchedEffect(state.orderPlaced) {
        if (state.orderPlaced) {
            onOrderPlaced()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Order",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Charcoal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Mustard,
                    titleContentColor = Charcoal
                )
            )
        },
        containerColor = Bun,
        modifier = modifier
    ) { innerPadding ->
        if (state.items.isEmpty()) {
            EmptyCartState(
                onNavigateBack = onNavigateBack,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Cart items
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.items,
                        key = { it.product.id ?: it.product.name }
                    ) { cartItem ->
                        OrderItemCard(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(cartItem.product.id ?: "", newQuantity)
                            },
                            onRemove = {
                                viewModel.removeProduct(cartItem.product.id ?: "")
                            }
                        )
                    }
                }

                // Order summary and place order button
                OrderSummary(
                    shop = state.shop,
                    totalAmount = state.totalAmount,
                    totalItems = state.totalItems,
                    deliveryAddress = state.deliveryAddress,
                    notes = state.notes,
                    isPlacingOrder = state.isPlacingOrder,
                    error = state.error,
                    onDeliveryAddressChange = { viewModel.updateDeliveryAddress(it) },
                    onNotesChange = { viewModel.updateNotes(it) },
                    onPlaceOrder = { viewModel.placeOrder() }
                )
            }
        }
    }
}

@Composable
private fun EmptyCartState(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dancing hotdog Easter egg! 🌭
        DancingHotdog(
            modifier = Modifier.size(120.dp),
            pixelSize = 5f
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your order is empty!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Charcoal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Browse available products and add them\nto your wholesale order.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Mustard,
                contentColor = Charcoal
            )
        ) {
            Text("Browse Products", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OrderItemCard(
    cartItem: OrderCartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            if (cartItem.product.imageUrl != null) {
                AsyncImage(
                    model = cartItem.product.imageUrl,
                    contentDescription = cartItem.product.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Bun),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Bun),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📦", style = MaterialTheme.typography.displaySmall)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product info and controls
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (cartItem.product.brand != null) {
                    Text(
                        text = cartItem.product.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${cartItem.product.wholesalePrice} per ${cartItem.product.unitsPerCase} units",
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity - 1) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Bun)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = Charcoal,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${cartItem.quantity}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Mustard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Charcoal,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Subtotal
                    Text(
                        text = "$${String.format("%.2f", cartItem.subtotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Ketchup
                    )
                }
            }

            // Delete button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Ketchup.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun OrderSummary(
    shop: com.indelo.goods.data.model.Shop?,
    totalAmount: Double,
    totalItems: Int,
    deliveryAddress: String,
    notes: String,
    isPlacingOrder: Boolean,
    error: String?,
    onDeliveryAddressChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onPlaceOrder: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Order Confirmation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            shop?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Charcoal.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery Address
            Text(
                text = "Delivery Address *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = deliveryAddress,
                onValueChange = onDeliveryAddressChange,
                placeholder = { Text("Enter delivery address") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Mustard,
                    unfocusedBorderColor = Charcoal.copy(alpha = 0.3f),
                    cursorColor = Ketchup,
                    focusedLabelColor = Mustard
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Special Instructions / Notes
            Text(
                text = "Special Instructions (optional)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                placeholder = { Text("Add delivery notes, special requests, etc.") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Mustard,
                    unfocusedBorderColor = Charcoal.copy(alpha = 0.3f),
                    cursorColor = Ketchup,
                    focusedLabelColor = Mustard
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Order Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total items:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Charcoal.copy(alpha = 0.7f)
                )
                Text(
                    text = "$totalItems",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Charcoal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order Total:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )
                Text(
                    text = "$${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Ketchup
                )
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ketchup,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlaceOrder,
                enabled = !isPlacingOrder,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ketchup,
                    contentColor = Color.White
                )
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Confirm Order",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "By placing this order, you confirm the delivery address and order details. The producer will review your order and arrange delivery. No payment required - you'll earn 4.5% of each sale when shoppers scan your QR codes!",
                style = MaterialTheme.typography.bodySmall,
                color = Charcoal.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
