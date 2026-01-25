package com.indelo.goods.ui.producer

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerOrdersScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProducerOrdersViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Shop Orders",
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
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Mustard)
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.error ?: "Unknown error",
                            color = Ketchup,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            state.orders.isEmpty() -> {
                EmptyOrdersState(modifier = Modifier.padding(innerPadding))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.orders,
                        key = { it.order.id ?: it.order.createdAt.toString() }
                    ) { orderWithItems ->
                        OrderCard(
                            orderWithItems = orderWithItems,
                            isUpdating = state.updatingOrderId == orderWithItems.order.id,
                            onAccept = { viewModel.updateOrderStatus(orderWithItems.order.id!!, "confirmed") },
                            onShip = { viewModel.updateOrderStatus(orderWithItems.order.id!!, "shipped") },
                            onComplete = { viewModel.updateOrderStatus(orderWithItems.order.id!!, "delivered") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyOrdersState(modifier: Modifier = Modifier) {
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
            text = "No orders yet!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Charcoal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Shops will place orders for your products here.\nYou'll be able to review and fulfill them.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OrderCard(
    orderWithItems: OrderWithItems,
    isUpdating: Boolean,
    onAccept: () -> Unit,
    onShip: () -> Unit,
    onComplete: () -> Unit
) {
    val order = orderWithItems.order
    val items = orderWithItems.items

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.id?.take(8)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                    order.createdAt?.let { createdAt ->
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        Text(
                            text = dateFormat.format(Date(createdAt.toLong())),
                            style = MaterialTheme.typography.bodySmall,
                            color = Charcoal.copy(alpha = 0.6f)
                        )
                    }
                }

                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery address
            order.shippingAddress?.let { address ->
                Text(
                    text = "Delivery Address:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Charcoal.copy(alpha = 0.6f)
                )
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Charcoal
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Order items
            Text(
                text = "Items:",
                style = MaterialTheme.typography.labelSmall,
                color = Charcoal.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Product image
                    if (item.productImageUrl != null) {
                        AsyncImage(
                            model = item.productImageUrl,
                            contentDescription = item.productName,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Bun),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Bun),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📦", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName ?: "Unknown Product",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Charcoal
                        )
                        Text(
                            text = "${item.quantity} cases",
                            style = MaterialTheme.typography.bodySmall,
                            color = Charcoal.copy(alpha = 0.6f)
                        )
                    }

                    Text(
                        text = "$${String.format("%.2f", item.subtotal)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Ketchup
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )
                Text(
                    text = "$${String.format("%.2f", order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ketchup
                )
            }

            // Notes
            order.notes?.let { notes ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Notes:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Charcoal.copy(alpha = 0.6f)
                )
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Bun, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                )
            }

            // Action buttons
            if (order.status != "delivered" && order.status != "cancelled") {
                Spacer(modifier = Modifier.height(16.dp))

                if (isUpdating) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Mustard
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (order.status) {
                            "pending" -> {
                                Button(
                                    onClick = onAccept,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Mustard,
                                        contentColor = Charcoal
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Accept", fontWeight = FontWeight.Bold)
                                }
                            }
                            "confirmed" -> {
                                Button(
                                    onClick = onShip,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Mustard,
                                        contentColor = Charcoal
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mark Shipped", fontWeight = FontWeight.Bold)
                                }
                            }
                            "shipped" -> {
                                Button(
                                    onClick = onComplete,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Ketchup,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mark Delivered", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (backgroundColor, textColor, displayText) = when (status) {
        "pending" -> Triple(Ketchup.copy(alpha = 0.2f), Ketchup, "Pending")
        "confirmed" -> Triple(Mustard.copy(alpha = 0.3f), Charcoal, "Confirmed")
        "shipped" -> Triple(Mustard.copy(alpha = 0.5f), Charcoal, "Shipped")
        "delivered" -> Triple(Mustard, Charcoal, "Delivered")
        "cancelled" -> Triple(Charcoal.copy(alpha = 0.2f), Charcoal, "Cancelled")
        else -> Triple(Charcoal.copy(alpha = 0.2f), Charcoal, status.capitalize())
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
