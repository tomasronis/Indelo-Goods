package com.indelo.goods.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Mustard

/**
 * Inventory Tracking Screen
 * Shows which shops have which products based on delivered orders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerInventoryScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inventory at Shops",
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
        ComingSoonPlaceholder(
            title = "Inventory Tracking",
            description = "Track your products across all shop locations.\nSee which shops have your inventory and stock levels.",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Sales Analytics Screen
 * Shows unit sales and revenue by shop and in aggregate
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerSalesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sales Analytics",
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
        ComingSoonPlaceholder(
            title = "Sales Analytics",
            description = "View your sales performance:\n• Units sold by product\n• Revenue by shop location\n• Aggregate totals and trends\n• Your 90% revenue share breakdown",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Payouts Screen
 * Shows payout history and upcoming payouts via Stripe Connect
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerPayoutsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Payouts",
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
        ComingSoonPlaceholder(
            title = "Payout History",
            description = "Track your earnings:\n• Upcoming payout dates and amounts\n• Payout history (90% of sales)\n• Bank account on file\n• Stripe Connect dashboard link",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Subscription Management Screen
 * Manage $50/month Indelo subscription and payment methods
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerSubscriptionScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Subscription",
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
        ComingSoonPlaceholder(
            title = "Subscription Management",
            description = "Manage your Indelo subscription:\n• $50/month plan details\n• Payment method on file\n• Billing history\n• Update payment method\n• Cancel subscription",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ComingSoonPlaceholder(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Dancing hotdog Easter egg! 🌭
            DancingHotdog(
                modifier = Modifier.size(100.dp),
                pixelSize = 4f
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Charcoal,
                textAlign = TextAlign.Center
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = "Coming Soon!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = com.indelo.goods.ui.theme.Ketchup,
                textAlign = TextAlign.Center
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Charcoal.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
