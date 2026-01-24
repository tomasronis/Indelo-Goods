package com.indelo.goods.ui.producer

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indelo.goods.data.model.ProductCategories
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.IndeloGoodsTheme
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

data class ProductFormState(
    // Basic Info
    val name: String = "",
    val brand: String = "",
    val shortDescription: String = "",
    val description: String = "",
    val category: String = "",

    // Pricing
    val wholesalePrice: String = "",
    val retailPrice: String = "",
    val unitsPerCase: String = "1",
    val minimumOrderQuantity: String = "1",

    // Specifications
    val volumeMl: String = "",
    val weightG: String = "",
    val servingSize: String = "",
    val servingsPerContainer: String = "",
    val shelfLifeDays: String = "",
    val countryOfOrigin: String = "",
    val storageInstructions: String = "",

    // Ingredients
    val ingredients: String = "",
    val allergens: String = "",

    // Certifications
    val isOrganic: Boolean = false,
    val isNonGmo: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isKosher: Boolean = false,

    // Inventory
    val sku: String = "",
    val upc: String = "",
    val leadTimeDays: String = ""
)

enum class ProductFormStep(val title: String, val subtitle: String) {
    BASIC_INFO("Basic Info", "Name, brand & description"),
    PRICING("Pricing", "Wholesale & retail prices"),
    SPECIFICATIONS("Specs", "Size, weight & details"),
    INGREDIENTS("Ingredients", "Contents & allergens"),
    CERTIFICATIONS("Certifications", "Organic, vegan, etc."),
    REVIEW("Review", "Check & submit")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreateScreen(
    onNavigateBack: () -> Unit,
    onProductCreated: (ProductFormState) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var formState by rememberSaveable { mutableStateOf(ProductFormState()) }
    val steps = ProductFormStep.entries

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Product",
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
        ) {
            // Step indicator
            StepIndicator(
                steps = steps,
                currentStep = currentStep,
                modifier = Modifier.padding(16.dp)
            )

            // Form content
            AnimatedContent(
                targetState = currentStep,
                label = "form_step"
            ) { step ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    when (steps[step]) {
                        ProductFormStep.BASIC_INFO -> BasicInfoStep(
                            formState = formState,
                            onFormStateChange = { formState = it }
                        )
                        ProductFormStep.PRICING -> PricingStep(
                            formState = formState,
                            onFormStateChange = { formState = it }
                        )
                        ProductFormStep.SPECIFICATIONS -> SpecificationsStep(
                            formState = formState,
                            onFormStateChange = { formState = it }
                        )
                        ProductFormStep.INGREDIENTS -> IngredientsStep(
                            formState = formState,
                            onFormStateChange = { formState = it }
                        )
                        ProductFormStep.CERTIFICATIONS -> CertificationsStep(
                            formState = formState,
                            onFormStateChange = { formState = it }
                        )
                        ProductFormStep.REVIEW -> ReviewStep(formState = formState)
                    }
                }
            }

            // Navigation buttons
            NavigationButtons(
                currentStep = currentStep,
                totalSteps = steps.size,
                isLoading = isLoading,
                canProceed = when (steps[currentStep]) {
                    ProductFormStep.BASIC_INFO -> formState.name.isNotBlank()
                    ProductFormStep.PRICING -> formState.wholesalePrice.isNotBlank()
                    else -> true
                },
                onBack = { if (currentStep > 0) currentStep-- },
                onNext = { if (currentStep < steps.size - 1) currentStep++ },
                onSubmit = { onProductCreated(formState) }
            )
        }
    }
}

@Composable
private fun StepIndicator(
    steps: List<ProductFormStep>,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index < currentStep
            val isCurrent = index == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> Mustard
                                isCurrent -> Ketchup
                                else -> Color.White
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = when {
                                isCompleted || isCurrent -> Color.Transparent
                                else -> Charcoal.copy(alpha = 0.3f)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Charcoal,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isCurrent) Color.White else Charcoal.copy(alpha = 0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) Charcoal else Charcoal.copy(alpha = 0.5f),
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    currentStep: Int,
    totalSteps: Int,
    isLoading: Boolean,
    canProceed: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentStep > 0) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Charcoal
                )
            ) {
                Text("Back")
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        if (isLoading) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Mustard)
            }
        } else {
            Button(
                onClick = if (currentStep == totalSteps - 1) onSubmit else onNext,
                enabled = canProceed,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentStep == totalSteps - 1) Ketchup else Mustard,
                    contentColor = if (currentStep == totalSteps - 1) Color.White else Charcoal
                )
            ) {
                Text(
                    text = if (currentStep == totalSteps - 1) "Create Product" else "Next",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasicInfoStep(
    formState: ProductFormState,
    onFormStateChange: (ProductFormState) -> Unit
) {
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf(
        ProductCategories.CANNED_VEGETABLES,
        ProductCategories.CANNED_FRUITS,
        ProductCategories.CANNED_BEANS,
        ProductCategories.CANNED_SOUPS,
        ProductCategories.CANNED_MEATS,
        ProductCategories.SAUCES,
        ProductCategories.BEVERAGES_SODA,
        ProductCategories.BEVERAGES_JUICE,
        ProductCategories.BEVERAGES_ENERGY,
        ProductCategories.BEVERAGES_SPARKLING,
        ProductCategories.BEVERAGES_TEA_COFFEE,
        ProductCategories.BEVERAGES_CRAFT,
        ProductCategories.PICKLED,
        ProductCategories.OTHER
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = "Basic Information", subtitle = "Tell us about your product")

        FormTextField(
            value = formState.name,
            onValueChange = { onFormStateChange(formState.copy(name = it)) },
            label = "Product Name *",
            placeholder = "e.g., Organic Tomato Sauce"
        )

        FormTextField(
            value = formState.brand,
            onValueChange = { onFormStateChange(formState.copy(brand = it)) },
            label = "Brand Name",
            placeholder = "Your brand name"
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        ) {
            OutlinedTextField(
                value = formState.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                placeholder = { Text("Select a category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                colors = formTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onFormStateChange(formState.copy(category = category))
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        FormTextField(
            value = formState.shortDescription,
            onValueChange = { onFormStateChange(formState.copy(shortDescription = it)) },
            label = "Short Description",
            placeholder = "Brief tagline (shown in listings)",
            singleLine = true
        )

        FormTextField(
            value = formState.description,
            onValueChange = { onFormStateChange(formState.copy(description = it)) },
            label = "Full Description",
            placeholder = "Detailed product description for customers...",
            singleLine = false,
            minLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PricingStep(
    formState: ProductFormState,
    onFormStateChange: (ProductFormState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = "Pricing & Packaging", subtitle = "Set your wholesale and retail prices")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                value = formState.wholesalePrice,
                onValueChange = { onFormStateChange(formState.copy(wholesalePrice = it)) },
                label = "Wholesale Price *",
                placeholder = "0.00",
                prefix = "$",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = formState.retailPrice,
                onValueChange = { onFormStateChange(formState.copy(retailPrice = it)) },
                label = "Suggested Retail",
                placeholder = "0.00",
                prefix = "$",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }

        SectionHeader(title = "Case Packaging", subtitle = "How products are packaged for wholesale")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                value = formState.unitsPerCase,
                onValueChange = { onFormStateChange(formState.copy(unitsPerCase = it)) },
                label = "Units per Case",
                placeholder = "12",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = formState.minimumOrderQuantity,
                onValueChange = { onFormStateChange(formState.copy(minimumOrderQuantity = it)) },
                label = "Min Order Qty",
                placeholder = "1",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }

        FormTextField(
            value = formState.leadTimeDays,
            onValueChange = { onFormStateChange(formState.copy(leadTimeDays = it)) },
            label = "Lead Time (days)",
            placeholder = "Days to fulfill order",
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SpecificationsStep(
    formState: ProductFormState,
    onFormStateChange: (ProductFormState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = "Product Specifications", subtitle = "Size, weight, and details")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                value = formState.volumeMl,
                onValueChange = { onFormStateChange(formState.copy(volumeMl = it)) },
                label = "Volume (ml)",
                placeholder = "355",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = formState.weightG,
                onValueChange = { onFormStateChange(formState.copy(weightG = it)) },
                label = "Weight (g)",
                placeholder = "400",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                value = formState.servingSize,
                onValueChange = { onFormStateChange(formState.copy(servingSize = it)) },
                label = "Serving Size",
                placeholder = "1 can (240ml)",
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = formState.servingsPerContainer,
                onValueChange = { onFormStateChange(formState.copy(servingsPerContainer = it)) },
                label = "Servings",
                placeholder = "1",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }

        SectionHeader(title = "Origin & Storage", subtitle = "")

        FormTextField(
            value = formState.countryOfOrigin,
            onValueChange = { onFormStateChange(formState.copy(countryOfOrigin = it)) },
            label = "Country of Origin",
            placeholder = "USA"
        )

        FormTextField(
            value = formState.shelfLifeDays,
            onValueChange = { onFormStateChange(formState.copy(shelfLifeDays = it)) },
            label = "Shelf Life (days)",
            placeholder = "365",
            keyboardType = KeyboardType.Number
        )

        FormTextField(
            value = formState.storageInstructions,
            onValueChange = { onFormStateChange(formState.copy(storageInstructions = it)) },
            label = "Storage Instructions",
            placeholder = "Store in a cool, dry place"
        )

        SectionHeader(title = "Inventory Codes", subtitle = "For tracking and scanning")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                value = formState.sku,
                onValueChange = { onFormStateChange(formState.copy(sku = it)) },
                label = "SKU",
                placeholder = "Your internal code",
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = formState.upc,
                onValueChange = { onFormStateChange(formState.copy(upc = it)) },
                label = "UPC/Barcode",
                placeholder = "12-digit code",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun IngredientsStep(
    formState: ProductFormState,
    onFormStateChange: (ProductFormState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = "Ingredients",
            subtitle = "List all ingredients for transparency"
        )

        FormTextField(
            value = formState.ingredients,
            onValueChange = { onFormStateChange(formState.copy(ingredients = it)) },
            label = "Ingredients List",
            placeholder = "Water, Organic Tomatoes, Sea Salt, Basil, Garlic...",
            singleLine = false,
            minLines = 4
        )

        SectionHeader(
            title = "Allergen Information",
            subtitle = "Important for customer safety"
        )

        FormTextField(
            value = formState.allergens,
            onValueChange = { onFormStateChange(formState.copy(allergens = it)) },
            label = "Allergens",
            placeholder = "Contains: Soy. May contain traces of: Wheat, Tree Nuts"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CertificationsStep(
    formState: ProductFormState,
    onFormStateChange: (ProductFormState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = "Certifications",
            subtitle = "Highlight what makes your product special"
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CertificationChip(
                label = "Organic",
                emoji = "\uD83C\uDF3F",
                isSelected = formState.isOrganic,
                onToggle = { onFormStateChange(formState.copy(isOrganic = !formState.isOrganic)) }
            )
            CertificationChip(
                label = "Non-GMO",
                emoji = "\uD83E\uDDEC",
                isSelected = formState.isNonGmo,
                onToggle = { onFormStateChange(formState.copy(isNonGmo = !formState.isNonGmo)) }
            )
            CertificationChip(
                label = "Vegan",
                emoji = "\uD83C\uDF31",
                isSelected = formState.isVegan,
                onToggle = { onFormStateChange(formState.copy(isVegan = !formState.isVegan)) }
            )
            CertificationChip(
                label = "Gluten-Free",
                emoji = "\uD83C\uDF3E",
                isSelected = formState.isGlutenFree,
                onToggle = { onFormStateChange(formState.copy(isGlutenFree = !formState.isGlutenFree)) }
            )
            CertificationChip(
                label = "Kosher",
                emoji = "✡️",
                isSelected = formState.isKosher,
                onToggle = { onFormStateChange(formState.copy(isKosher = !formState.isKosher)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CertificationChip(
    label: String,
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
                text = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = Charcoal
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

@Composable
private fun ReviewStep(formState: ProductFormState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = "Review Your Product",
            subtitle = "Make sure everything looks good"
        )

        ReviewCard(title = "Basic Info") {
            ReviewItem("Name", formState.name)
            ReviewItem("Brand", formState.brand.ifBlank { "—" })
            ReviewItem("Category", formState.category.ifBlank { "—" })
            if (formState.shortDescription.isNotBlank()) {
                ReviewItem("Tagline", formState.shortDescription)
            }
        }

        ReviewCard(title = "Pricing") {
            ReviewItem("Wholesale", "$${formState.wholesalePrice}")
            ReviewItem("Retail (suggested)", if (formState.retailPrice.isBlank()) "—" else "$${formState.retailPrice}")
            ReviewItem("Units/Case", formState.unitsPerCase)
            ReviewItem("Min Order", formState.minimumOrderQuantity)
        }

        ReviewCard(title = "Specifications") {
            if (formState.volumeMl.isNotBlank()) ReviewItem("Volume", "${formState.volumeMl} ml")
            if (formState.weightG.isNotBlank()) ReviewItem("Weight", "${formState.weightG} g")
            if (formState.countryOfOrigin.isNotBlank()) ReviewItem("Origin", formState.countryOfOrigin)
            if (formState.shelfLifeDays.isNotBlank()) ReviewItem("Shelf Life", "${formState.shelfLifeDays} days")
        }

        val certifications = buildList {
            if (formState.isOrganic) add("Organic")
            if (formState.isNonGmo) add("Non-GMO")
            if (formState.isVegan) add("Vegan")
            if (formState.isGlutenFree) add("Gluten-Free")
            if (formState.isKosher) add("Kosher")
        }

        if (certifications.isNotEmpty()) {
            ReviewCard(title = "Certifications") {
                Text(
                    text = certifications.joinToString(" • "),
                    color = Charcoal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ReviewCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Ketchup
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun ReviewItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Charcoal.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            color = Charcoal,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Charcoal
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Charcoal.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        prefix = prefix?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = if (singleLine) ImeAction.Next else ImeAction.Default
        ),
        colors = formTextFieldColors(),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Mustard,
    unfocusedBorderColor = Charcoal.copy(alpha = 0.3f),
    cursorColor = Ketchup,
    focusedLabelColor = Mustard,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)

@Preview(showBackground = true)
@Composable
fun ProductCreateScreenPreview() {
    IndeloGoodsTheme {
        ProductCreateScreen(
            onNavigateBack = {},
            onProductCreated = { _ -> }
        )
    }
}
