package com.indelo.goods.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.indelo.goods.data.model.Order
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopOrderHistoryScreen(
    shopId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShopOrderHistoryViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(shopId) {
        viewModel.loadOrdersForShop(shopId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order History",
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
                    Text(
                        text = state.error ?: "Unknown error",
                        color = Ketchup,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
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
                    items(state.orders) { order ->
                        OrderCard(order = order)
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
            text = "Place your first wholesale order to get started.\nBrowse products and add them to your cart!",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OrderCard(order: Order) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Order header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${order.id?.take(8)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                    Text(
                        text = formatDate(order.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.6f)
                    )
                }

                // Status badge
                StatusBadge(status = order.status ?: "pending")
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Charcoal.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            // Delivery address
            if (order.shippingAddress != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Delivery:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.6f),
                        modifier = Modifier.width(80.dp)
                    )
                    Text(
                        text = order.shippingAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Notes
            if (order.notes != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Notes:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.6f),
                        modifier = Modifier.width(80.dp)
                    )
                    Text(
                        text = order.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (backgroundColor, textColor, displayText) = when (status.lowercase()) {
        "pending" -> Triple(Mustard.copy(alpha = 0.3f), Charcoal, "Pending")
        "confirmed" -> Triple(Mustard, Charcoal, "Confirmed")
        "shipped" -> Triple(Color(0xFF4CAF50).copy(alpha = 0.3f), Charcoal, "Shipped")
        "delivered" -> Triple(Color(0xFF4CAF50), Color.White, "Delivered")
        "cancelled" -> Triple(Ketchup.copy(alpha = 0.3f), Charcoal, "Cancelled")
        else -> Triple(Charcoal.copy(alpha = 0.2f), Charcoal, status.capitalize())
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString == null) return "Unknown date"

    return try {
        val instant = Instant.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        dateString
    }
}
