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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
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
import com.indelo.goods.data.model.Product
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopProductBrowseScreen(
    shopId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductBrowseViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(shopId) {
        viewModel.loadProductsForShop(shopId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Browse Products",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        state.shop?.let { shop ->
                            Text(
                                text = shop.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = Charcoal.copy(alpha = 0.7f)
                            )
                        }
                    }
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
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "View Cart",
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
            state.products.isEmpty() -> {
                EmptyProductsState(
                    shopCity = state.shop?.city,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.products,
                        key = { it.id ?: it.name }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onAddToCart = { onAddToCart(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProductsState(
    shopCity: String?,
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
            text = "No products available yet!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Charcoal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (shopCity != null) {
                "No producers have added products for $shopCity yet.\nCheck back soon!"
            } else {
                "No producers have added products for your area yet.\nCheck back soon!"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Product image
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Bun),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Bun),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📦",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product name
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Charcoal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                minLines = 2
            )

            // Brand
            if (product.brand != null) {
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Packaging info - FREE on consignment!
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "FREE",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Ketchup
                    )
                    Text(
                        text = "${product.unitsPerCase} units/case",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add to cart button
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Mustard,
                    contentColor = Charcoal
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
