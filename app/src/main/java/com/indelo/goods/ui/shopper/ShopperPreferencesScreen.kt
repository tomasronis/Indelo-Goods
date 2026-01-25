package com.indelo.goods.ui.shopper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.indelo.goods.data.model.ProductCategories
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShopperPreferencesScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ShopperPreferencesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedCategories by remember { mutableStateOf(state.preferences?.favoriteCategories ?: emptyList()) }
    var notificationsEnabled by remember { mutableStateOf(state.preferences?.notificationsEnabled ?: false) }

    val categories = listOf(
        "Canned Vegetables" to "🥕",
        "Canned Fruits" to "🍑",
        "Canned Beans & Legumes" to "🫘",
        "Soups & Broths" to "🍲",
        "Canned Meats & Seafood" to "🐟",
        "Sauces & Condiments" to "🍅",
        "Sodas & Soft Drinks" to "🥤",
        "Juices" to "🧃",
        "Energy Drinks" to "⚡",
        "Sparkling Water" to "💧",
        "Ready-to-Drink Tea & Coffee" to "☕",
        "Craft Beverages" to "🍺",
        "Pickled & Fermented" to "🥒",
        "Other" to "📦"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Preferences",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Favorite Categories",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select the types of products you love! We'll show you personalized recommendations.",
                style = MaterialTheme.typography.bodyMedium,
                color = Charcoal.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (category, emoji) ->
                    CategoryChip(
                        category = category,
                        emoji = emoji,
                        isSelected = selectedCategories.contains(category),
                        onToggle = {
                            selectedCategories = if (selectedCategories.contains(category)) {
                                selectedCategories - category
                            } else {
                                selectedCategories + category
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Mustard,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "New Product Notifications",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal
                            )
                            Text(
                                text = "Get notified when new products match your interests",
                                style = MaterialTheme.typography.bodySmall,
                                color = Charcoal.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Mustard,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Charcoal.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    viewModel.savePreferences(selectedCategories, notificationsEnabled)
                    if (state.isSaved) {
                        onSaved()
                    }
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ketchup,
                    contentColor = Color.White
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Save Preferences",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ketchup,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: String,
    emoji: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Mustard else Color.White)
            .border(
                width = 2.dp,
                color = if (isSelected) Ketchup else Charcoal.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = Charcoal,
                style = MaterialTheme.typography.bodyMedium
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Ketchup
                )
            }
        }
    }
}
