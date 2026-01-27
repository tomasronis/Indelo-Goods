package com.indelo.goods.ui.producer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerProfileEditScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProducerProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Form state
    var companyName by rememberSaveable { mutableStateOf("") }
    var brandName by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }
    var background by rememberSaveable { mutableStateOf("") }
    var inspiration by rememberSaveable { mutableStateOf("") }
    var goals by rememberSaveable { mutableStateOf("") }
    var websiteUrl by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var foundedYear by rememberSaveable { mutableStateOf("") }
    var specialty by rememberSaveable { mutableStateOf("") }

    // Load existing profile data
    LaunchedEffect(state.profile) {
        state.profile?.let { profile ->
            companyName = profile.companyName ?: ""
            brandName = profile.brandName ?: ""
            bio = profile.bio ?: ""
            background = profile.background ?: ""
            inspiration = profile.inspiration ?: ""
            goals = profile.goals ?: ""
            websiteUrl = profile.websiteUrl ?: ""
            location = profile.location ?: ""
            foundedYear = profile.foundedYear?.toString() ?: ""
            specialty = profile.specialty ?: ""
        }
    }

    // Handle success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Profile saved successfully!")
            viewModel.clearSuccess()
        }
    }

    // Handle error
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.profile == null) "Create Profile" else "Edit Profile",
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Bun,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Company Information
            Text(
                text = "Company Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ketchup
            )

            ProfileTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = "Company Name",
                placeholder = "e.g., Artisan Foods Co."
            )

            ProfileTextField(
                value = brandName,
                onValueChange = { brandName = it },
                label = "Brand Name",
                placeholder = "e.g., Artisan"
            )

            ProfileTextField(
                value = location,
                onValueChange = { location = it },
                label = "Location",
                placeholder = "e.g., Portland, OR"
            )

            ProfileTextField(
                value = foundedYear,
                onValueChange = { foundedYear = it },
                label = "Founded Year",
                placeholder = "e.g., 2020",
                keyboardType = KeyboardType.Number
            )

            ProfileTextField(
                value = specialty,
                onValueChange = { specialty = it },
                label = "Specialty",
                placeholder = "e.g., Organic Canned Vegetables"
            )

            ProfileTextField(
                value = websiteUrl,
                onValueChange = { websiteUrl = it },
                label = "Website URL",
                placeholder = "https://yourwebsite.com",
                keyboardType = KeyboardType.Uri
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Section: Your Story
            Text(
                text = "Your Story",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ketchup
            )

            ProfileTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "Bio",
                placeholder = "A brief introduction to your company...",
                singleLine = false,
                minLines = 3
            )

            ProfileTextField(
                value = background,
                onValueChange = { background = it },
                label = "Background",
                placeholder = "Tell us about your journey and experience...",
                singleLine = false,
                minLines = 4
            )

            ProfileTextField(
                value = inspiration,
                onValueChange = { inspiration = it },
                label = "Inspiration",
                placeholder = "What inspired you to start this business?",
                singleLine = false,
                minLines = 3
            )

            ProfileTextField(
                value = goals,
                onValueChange = { goals = it },
                label = "Goals",
                placeholder = "What are your goals and vision?",
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = {
                    viewModel.saveProfile(
                        companyName = companyName,
                        brandName = brandName,
                        bio = bio,
                        background = background,
                        inspiration = inspiration,
                        goals = goals,
                        websiteUrl = websiteUrl,
                        location = location,
                        foundedYear = foundedYear.toIntOrNull(),
                        specialty = specialty
                    )
                },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ketchup,
                    contentColor = Color.White
                )
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    text = if (state.isSaving) "Saving..." else "Save Profile",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProfileTextField(
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Mustard,
            unfocusedBorderColor = Charcoal.copy(alpha = 0.3f),
            cursorColor = Ketchup,
            focusedLabelColor = Mustard,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Charcoal,
            unfocusedTextColor = Charcoal
        ),
        modifier = modifier.fillMaxWidth()
    )
}
