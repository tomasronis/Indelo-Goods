package com.indelo.goods.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch
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
    onNavigateToProductDetails: (String) -> Unit,
    onAddToCart: (Product, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductBrowseViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(shopId) {
        viewModel.loadProductsForShop(shopId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                var isSearchFocused by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // AI Search Bar - expands when focused
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { query ->
                            if (query.isBlank()) {
                                viewModel.clearSearch()
                            } else {
                                viewModel.searchWithAI(query)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .onFocusChanged { focusState ->
                                isSearchFocused = focusState.isFocused
                            },
                        placeholder = {
                            Text("Ask AI: \"organic drinks\" or \"gluten-free snacks\"...")
                        },
                        leadingIcon = {
                            if (state.isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Mustard,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Charcoal
                                )
                            }
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearSearch() }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = Charcoal
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Mustard,
                            unfocusedIndicatorColor = Charcoal.copy(alpha = 0.3f),
                            cursorColor = Ketchup
                        ),
                        singleLine = !isSearchFocused,
                        maxLines = if (isSearchFocused) 3 else 1,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Product Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.products,
                            key = { it.id ?: it.name }
                        ) { product ->
                            ProductCard(
                                product = product,
                                onProductClick = { onNavigateToProductDetails(product.id ?: "") },
                                onAddToCart = { quantity ->
                                    onAddToCart(product, quantity)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Added ${product.name} (${quantity} units) to cart",
                                            duration = androidx.compose.material3.SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        }
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
    onProductClick: () -> Unit,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember {
        mutableIntStateOf(product.minimumOrderQuantity)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Product image - clickable to view details
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Bun)
                        .clickable { onProductClick() },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Bun)
                        .clickable { onProductClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📦",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product name - clickable to view details
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Charcoal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                minLines = 2,
                modifier = Modifier.clickable { onProductClick() }
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

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity selector - centered and larger
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Decrease button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (quantity > product.minimumOrderQuantity) Bun else Bun.copy(alpha = 0.3f))
                        .clickable(enabled = quantity > product.minimumOrderQuantity) {
                            quantity--
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Decrease quantity",
                        tint = if (quantity > product.minimumOrderQuantity) Charcoal else Charcoal.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Quantity display
                Text(
                    text = "$quantity",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center
                )

                // Increase button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Mustard)
                        .clickable { quantity++ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase quantity",
                        tint = Charcoal,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add to cart button (full width at bottom)
            Button(
                onClick = {
                    onAddToCart(quantity)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ketchup,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Add to Cart",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
