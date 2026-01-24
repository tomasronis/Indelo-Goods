package com.indelo.goods.ui.producer

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.IndeloGoodsTheme
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = viewModel()
) {
    val editState by viewModel.editState.collectAsState()
    var formState by rememberSaveable { mutableStateOf(ProductFormState()) }
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var isInitialized by rememberSaveable { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val steps = ProductFormStep.entries

    // Load product when screen opens
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    // Pre-populate form when product loads
    LaunchedEffect(editState.product) {
        if (editState.product != null && !isInitialized) {
            formState = editState.product!!.toFormState()
            isInitialized = true
        }
    }

    // Handle success
    LaunchedEffect(editState.isSuccess) {
        if (editState.isSuccess) {
            viewModel.clearEditState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Product",
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
        if (editState.isLoading && editState.product == null) {
            // Loading product initially
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Mustard)
            }
        } else if (editState.error != null && editState.product == null) {
            // Error loading product
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error loading product",
                        color = Ketchup,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = editState.error ?: "Unknown error",
                        color = Charcoal,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // Show form
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
                                onFormStateChange = { formState = it },
                                selectedImageUri = selectedImageUri,
                                onImageSelected = { selectedImageUri = it }
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
                EditNavigationButtons(
                    currentStep = currentStep,
                    totalSteps = steps.size,
                    isLoading = editState.isLoading && editState.product != null,
                    canProceed = when (steps[currentStep]) {
                        ProductFormStep.BASIC_INFO -> formState.name.isNotBlank()
                        ProductFormStep.PRICING -> formState.wholesalePrice.isNotBlank()
                        else -> true
                    },
                    onBack = { if (currentStep > 0) currentStep-- },
                    onNext = { if (currentStep < steps.size - 1) currentStep++ },
                    onSubmit = { viewModel.updateProduct(productId, formState, selectedImageUri) }
                )
            }
        }
    }
}

@Composable
private fun EditNavigationButtons(
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
                    text = if (currentStep == totalSteps - 1) "Update Product" else "Next",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductEditScreenPreview() {
    IndeloGoodsTheme {
        ProductEditScreen(
            productId = "test-id",
            onNavigateBack = {}
        )
    }
}
