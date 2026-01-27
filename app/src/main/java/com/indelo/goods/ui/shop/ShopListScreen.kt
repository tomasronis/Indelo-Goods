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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.indelo.goods.data.model.Shop
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopListScreen(
    onSignOut: () -> Unit,
    onCreateShop: () -> Unit,
    onShopClick: (String) -> Unit,
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val listState by viewModel.listState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Shops",
                        fontWeight = FontWeight.Bold
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
                onClick = onCreateShop,
                containerColor = Ketchup,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Shop"
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
            listState.shops.isEmpty() -> {
                EmptyShopsState(
                    onCreateShop = onCreateShop,
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
                    items(
                        items = listState.shops,
                        key = { it.id ?: it.name }
                    ) { shop ->
                        ShopCard(
                            shop = shop,
                            onClick = { shop.id?.let(onShopClick) },
                            onDelete = { shop.id?.let { viewModel.deleteShop(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyShopsState(
    onCreateShop: () -> Unit,
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
            text = "No shops yet!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Charcoal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add your cafe, restaurant, or store\nto start ordering wholesale products.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateShop,
            colors = ButtonDefaults.buttonColors(
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
            Text("Create Your First Shop", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ShopCard(
    shop: Shop,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
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
            // Shop icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Mustard.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Ketchup,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shop.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                shop.businessType?.let { type ->
                    Text(
                        text = type,
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val location = buildList {
                        shop.city?.let { add(it) }
                        shop.state?.let { add(it) }
                    }.joinToString(", ")

                    if (location.isNotEmpty()) {
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodySmall,
                            color = Charcoal.copy(alpha = 0.7f)
                        )
                    }
                }
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
}
