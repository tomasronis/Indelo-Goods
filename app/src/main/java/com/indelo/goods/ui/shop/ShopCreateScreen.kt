package com.indelo.goods.ui.shop

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.indelo.goods.data.model.Shop
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

data class ShopFormData(
    // Basic Info
    val name: String = "",
    val businessType: String = "",
    val description: String = "",

    // Location
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "USA",

    // Contact & Business
    val phone: String = "",
    val email: String = "",
    val taxId: String = ""
)

enum class ShopFormStep(val title: String, val subtitle: String) {
    BASIC_INFO("Basic", "Shop details"),
    LOCATION("Location", "Address"),
    CONTACT("Contact", "Info & tax ID"),
    REVIEW("Review", "Confirm")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopCreateScreen(
    onNavigateBack: () -> Unit,
    onShopCreated: (ShopFormData) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var formState by rememberSaveable { mutableStateOf(ShopFormData()) }
    val steps = ShopFormStep.entries

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Shop",
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
                label = "shop_form_step"
            ) { step ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    when (steps[step]) {
                        ShopFormStep.BASIC_INFO -> BasicInfoStep(
                            formState = formState,
                            onFormStateChange = { formState = it }
                        )
                        ShopFormStep.LOCATION -> LocationStep(
                            formState = formState,
                            onFormStateChange = { formState = it }
                        )
                        ShopFormStep.CONTACT -> ContactStep(
                            formState = formState,
                            onFormStateChange = { formState = it }
                        )
                        ShopFormStep.REVIEW -> ReviewStep(formState = formState)
                    }
                }
            }

            // Navigation buttons
            NavigationButtons(
                currentStep = currentStep,
                totalSteps = steps.size,
                isLoading = isLoading,
                canProceed = when (steps[currentStep]) {
                    ShopFormStep.BASIC_INFO -> formState.name.isNotBlank()
                    ShopFormStep.LOCATION -> formState.city.isNotBlank()
                    else -> true
                },
                onBack = { if (currentStep > 0) currentStep-- },
                onNext = { if (currentStep < steps.size - 1) currentStep++ },
                onSubmit = { onShopCreated(formState) }
            )
        }
    }
}

@Composable
private fun StepIndicator(
    steps: List<ShopFormStep>,
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
                    text = if (currentStep == totalSteps - 1) "Create Shop" else "Next",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasicInfoStep(
    formState: ShopFormData,
    onFormStateChange: (ShopFormData) -> Unit
) {
    var businessTypeExpanded by remember { mutableStateOf(false) }
    val businessTypes = listOf(
        "Cafe",
        "Coffee Shop",
        "Restaurant",
        "Bar",
        "Retail Store",
        "Grocery Store",
        "Convenience Store",
        "Hotel",
        "Catering Service",
        "Food Truck",
        "Other"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = "Basic Information", subtitle = "Tell us about your shop")

        FormTextField(
            value = formState.name,
            onValueChange = { onFormStateChange(formState.copy(name = it)) },
            label = "Shop Name *",
            placeholder = "e.g., The Daily Grind Cafe"
        )

        ExposedDropdownMenuBox(
            expanded = businessTypeExpanded,
            onExpandedChange = { businessTypeExpanded = it }
        ) {
            OutlinedTextField(
                value = formState.businessType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Business Type") },
                placeholder = { Text("Select type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = businessTypeExpanded) },
                colors = formTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = businessTypeExpanded,
                onDismissRequest = { businessTypeExpanded = false }
            ) {
                businessTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            onFormStateChange(formState.copy(businessType = type))
                            businessTypeExpanded = false
                        }
                    )
                }
            }
        }

        FormTextField(
            value = formState.description,
            onValueChange = { onFormStateChange(formState.copy(description = it)) },
            label = "Description (optional)",
            placeholder = "Brief description of your shop...",
            singleLine = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LocationStep(
    formState: ShopFormData,
    onFormStateChange: (ShopFormData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = "Shop Location",
            subtitle = "Where can producers find you?"
        )

        FormTextField(
            value = formState.address,
            onValueChange = { onFormStateChange(formState.copy(address = it)) },
            label = "Street Address",
            placeholder = "123 Main Street"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                value = formState.city,
                onValueChange = { onFormStateChange(formState.copy(city = it)) },
                label = "City *",
                placeholder = "San Francisco",
                modifier = Modifier.weight(2f)
            )
            FormTextField(
                value = formState.state,
                onValueChange = { onFormStateChange(formState.copy(state = it)) },
                label = "State",
                placeholder = "CA",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField(
                value = formState.zipCode,
                onValueChange = { onFormStateChange(formState.copy(zipCode = it)) },
                label = "Zip Code",
                placeholder = "94102",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = formState.country,
                onValueChange = { onFormStateChange(formState.copy(country = it)) },
                label = "Country",
                placeholder = "USA",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ContactStep(
    formState: ShopFormData,
    onFormStateChange: (ShopFormData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = "Contact & Business Info",
            subtitle = "For orders and invoicing"
        )

        FormTextField(
            value = formState.phone,
            onValueChange = { onFormStateChange(formState.copy(phone = it)) },
            label = "Phone Number",
            placeholder = "(555) 123-4567",
            keyboardType = KeyboardType.Phone
        )

        FormTextField(
            value = formState.email,
            onValueChange = { onFormStateChange(formState.copy(email = it)) },
            label = "Email",
            placeholder = "contact@yourshop.com",
            keyboardType = KeyboardType.Email
        )

        SectionHeader(
            title = "Business Details",
            subtitle = "For wholesale ordering"
        )

        FormTextField(
            value = formState.taxId,
            onValueChange = { onFormStateChange(formState.copy(taxId = it)) },
            label = "Tax ID / EIN (optional)",
            placeholder = "12-3456789"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ReviewStep(formState: ShopFormData) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = "Review Your Shop",
            subtitle = "Make sure everything looks good"
        )

        // Dancing hotdog Easter egg! 🌭
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DancingHotdog(
                modifier = Modifier.size(80.dp),
                pixelSize = 4f
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ReviewCard(title = "Basic Info") {
            ReviewItem("Name", formState.name)
            ReviewItem("Type", formState.businessType.ifBlank { "—" })
            if (formState.description.isNotBlank()) {
                ReviewItem("Description", formState.description)
            }
        }

        ReviewCard(title = "Location") {
            if (formState.address.isNotBlank()) ReviewItem("Address", formState.address)
            ReviewItem("City", formState.city)
            if (formState.state.isNotBlank()) ReviewItem("State", formState.state)
            if (formState.zipCode.isNotBlank()) ReviewItem("Zip", formState.zipCode)
            ReviewItem("Country", formState.country)
        }

        if (formState.phone.isNotBlank() || formState.email.isNotBlank() || formState.taxId.isNotBlank()) {
            ReviewCard(title = "Contact & Business") {
                if (formState.phone.isNotBlank()) ReviewItem("Phone", formState.phone)
                if (formState.email.isNotBlank()) ReviewItem("Email", formState.email)
                if (formState.taxId.isNotBlank()) ReviewItem("Tax ID", formState.taxId)
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = Charcoal.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodySmall
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
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
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
