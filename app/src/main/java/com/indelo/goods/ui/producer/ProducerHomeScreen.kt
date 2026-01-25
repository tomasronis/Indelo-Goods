package com.indelo.goods.ui.producer

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.indelo.goods.data.model.Product
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.components.QrCodeDialog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.IndeloGoodsTheme
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerHomeScreen(
    onSignOut: () -> Unit,
    onCreateProduct: () -> Unit,
    onProductClick: (String) -> Unit,
    onViewOrders: () -> Unit = {},
    onViewSales: () -> Unit = {},
    onViewInventory: () -> Unit = {},
    onViewPayouts: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = viewModel()
) {
    val listState by viewModel.listState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Products",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateProduct,
                containerColor = Ketchup,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product"
                )
            }
        },
        containerColor = Bun,
        modifier = modifier
    ) { innerPadding ->
        when {
            listState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Mustard)
                }
            }
            listState.products.isEmpty() -> {
                EmptyProductsState(
                    onCreateProduct = onCreateProduct,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        QuickActionsSection(
                            onViewOrders = onViewOrders,
                            onViewSales = onViewSales,
                            onViewInventory = onViewInventory,
                            onViewPayouts = onViewPayouts
                        )
                    }

                    item {
                        Text(
                            text = "My Products",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(
                        items = listState.products,
                        key = { it.id ?: it.name }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onClick = { product.id?.let(onProductClick) },
                            onDelete = { product.id?.let { viewModel.deleteProduct(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProductsState(
    onCreateProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            text = "No products yet!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Charcoal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start adding your delicious canned goods\nand beverages to share with the world.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        androidx.compose.material3.Button(
            onClick = onCreateProduct,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Mustard,
                contentColor = Charcoal
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Your First Product", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuickActionsSection(
    onViewOrders: () -> Unit,
    onViewSales: () -> Unit,
    onViewInventory: () -> Unit,
    onViewPayouts: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Charcoal,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.ShoppingBag,
                label = "Orders",
                onClick = onViewOrders,
                modifier = Modifier.weight(1f)
            )

            QuickActionCard(
                icon = Icons.Default.BarChart,
                label = "Sales",
                onClick = onViewSales,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.Inventory,
                label = "Inventory",
                onClick = onViewInventory,
                modifier = Modifier.weight(1f)
            )

            QuickActionCard(
                icon = Icons.Default.AccountBalanceWallet,
                label = "Payouts",
                onClick = onViewPayouts,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color.White else Color.White.copy(alpha = 0.5f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) Mustard else Charcoal.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Charcoal else Charcoal.copy(alpha = 0.3f)
            )
            if (!enabled) {
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.labelSmall,
                    color = Charcoal.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showQrDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Mustard.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(product.imageUrl),
                        contentDescription = "Product image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = product.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                product.brand?.let { brand ->
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriceTag(
                        label = "Wholesale",
                        price = "$${String.format("%.2f", product.wholesalePrice)}"
                    )
                    product.retailPrice?.let { retail ->
                        PriceTag(
                            label = "Retail",
                            price = "$${String.format("%.2f", retail)}"
                        )
                    }
                }

                // Certification badges
                val badges = buildList {
                    if (product.isOrganic) add("🌿")
                    if (product.isVegan) add("🌱")
                    if (product.isGlutenFree) add("🌾")
                }
                if (badges.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = badges.joinToString(" "), fontSize = 14.sp)
                }
            }

            // QR Code Button
            IconButton(onClick = { showQrDialog = true }) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "Show QR Code",
                    tint = Mustard
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Ketchup.copy(alpha = 0.7f)
                )
            }
        }
    }

    // QR Code Dialog
    if (showQrDialog && product.id != null) {
        QrCodeDialog(
            productId = product.id,
            productName = product.name,
            onDismiss = { showQrDialog = false }
        )
    }
}

@Composable
private fun PriceTag(label: String, price: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelSmall,
            color = Charcoal.copy(alpha = 0.5f)
        )
        Text(
            text = price,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Ketchup
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProducerHomeScreenPreview() {
    IndeloGoodsTheme {
        ProducerHomeScreen(
            onSignOut = {},
            onCreateProduct = {},
            onProductClick = {}
        )
    }
}
