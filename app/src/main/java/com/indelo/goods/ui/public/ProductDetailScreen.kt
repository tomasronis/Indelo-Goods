package com.indelo.goods.ui.public

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.indelo.goods.data.model.Product
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.producer.ProductViewModel
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProducer: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = viewModel()
) {
    val editState by viewModel.editState.collectAsState()

    // Load product
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", fontWeight = FontWeight.Bold) },
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
            editState.isLoading && editState.product == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Mustard)
                }
            }
            editState.error != null && editState.product == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Dancing hotdog easter egg for errors!
                        DancingHotdog(
                            modifier = Modifier.size(120.dp),
                            pixelSize = 5f
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Product not found",
                            color = Ketchup,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = editState.error ?: "Unknown error",
                            color = Charcoal.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            editState.product != null -> {
                ProductDetailContent(
                    product = editState.product!!,
                    onNavigateToProducer = onNavigateToProducer,
                    onAddToCart = onAddToCart,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductDetailContent(
    product: Product,
    onNavigateToProducer: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Product Image
        if (product.imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(product.imageUrl),
                contentDescription = "Product image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Mustard.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.take(2).uppercase(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Product Name and Brand
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            product.brand?.let { brand ->
                Text(
                    text = brand,
                    style = MaterialTheme.typography.titleMedium,
                    color = Charcoal.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Certifications
            val badges = buildList {
                if (product.isOrganic) add("🌿 Organic")
                if (product.isVegan) add("🌱 Vegan")
                if (product.isGlutenFree) add("🌾 Gluten-Free")
                if (product.isNonGmo) add("🧬 Non-GMO")
                if (product.isKosher) add("✡️ Kosher")
            }

            if (badges.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    badges.forEach { badge ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Mustard.copy(alpha = 0.3f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelMedium,
                                color = Charcoal
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Price
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.titleSmall,
                        color = Charcoal.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$${String.format("%.2f", product.retailPrice ?: product.wholesalePrice)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Ketchup
                    )
                    product.retailPrice?.let {
                        Text(
                            text = "Wholesale: $${String.format("%.2f", product.wholesalePrice)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Charcoal.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            product.shortDescription?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = Charcoal,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            product.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Charcoal.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Product Specifications
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Ketchup
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    product.volumeMl?.let {
                        SpecRow("Volume", "$it ml")
                    }
                    product.weightG?.let {
                        SpecRow("Weight", "$it g")
                    }
                    product.servingSize?.let {
                        SpecRow("Serving Size", it)
                    }
                    product.countryOfOrigin?.let {
                        SpecRow("Origin", it)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ingredients
            product.ingredients?.let { ingredients ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ingredients",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Ketchup
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ingredients,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Charcoal
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Producer Info
            product.producerId?.let { producerId ->
                Card(
                    onClick = { onNavigateToProducer(producerId) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Ketchup.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (product.brand ?: "P").take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Ketchup
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "From ${product.brand ?: "this producer"}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal
                            )
                            Text(
                                text = "View all products",
                                style = MaterialTheme.typography.bodySmall,
                                color = Ketchup
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom button
        }

        // Fixed bottom button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Bun)
                .padding(16.dp)
        ) {
            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ketchup,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add to Cart",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Charcoal
        )
    }
}
