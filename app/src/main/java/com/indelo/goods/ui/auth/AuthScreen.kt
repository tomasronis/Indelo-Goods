package com.indelo.goods.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indelo.goods.data.model.UserType
import com.indelo.goods.ui.components.DancingHotdog
import com.indelo.goods.ui.theme.Bun
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.IndeloGoodsTheme
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onSendOtp: (phone: String) -> Unit,
    onVerifyOtp: (token: String) -> Unit,
    onSelectUserType: (UserType) -> Unit,
    onGoBack: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Bun
    ) { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Bun
        ) {
            when (uiState.step) {
                AuthStep.PHONE_ENTRY -> PhoneEntryScreen(
                    isLoading = uiState.isLoading,
                    onSendOtp = onSendOtp
                )
                AuthStep.OTP_VERIFICATION -> OtpVerificationScreen(
                    phone = uiState.phone,
                    isLoading = uiState.isLoading,
                    onVerifyOtp = onVerifyOtp,
                    onGoBack = onGoBack,
                    onResendOtp = { onSendOtp(uiState.phone) }
                )
                AuthStep.USER_TYPE_SELECTION -> UserTypeSelectionScreen(
                    selectedType = uiState.selectedUserType,
                    onSelectUserType = onSelectUserType,
                    onGoBack = onGoBack
                )
            }
        }
    }
}

@Composable
private fun PhoneEntryScreen(
    isLoading: Boolean,
    onSendOtp: (String) -> Unit
) {
    // Store only digits (no formatting)
    var phoneDigits by rememberSaveable { mutableStateOf("") }

    // Format for display: (XXX) XXX-XXXX
    fun formatPhoneNumber(digits: String): String {
        return when (digits.length) {
            0 -> ""
            in 1..3 -> "($digits"
            in 4..6 -> "(${digits.substring(0, 3)}) ${digits.substring(3)}"
            in 7..10 -> "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
            else -> "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6, 10)}"
        }
    }

    val displayValue = formatPhoneNumber(phoneDigits)
    val phoneWithCountryCode = if (phoneDigits.isEmpty()) "" else "+1$phoneDigits"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Dancing Hotdog
        DancingHotdog(
            modifier = Modifier.size(160.dp),
            pixelSize = 6f
        )

        Spacer(modifier = Modifier.height(24.dp))

        // App name - retro style
        Text(
            text = "INDELO",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            ),
            color = Ketchup
        )
        Text(
            text = "GOODS",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp
            ),
            color = Charcoal
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Enter your phone number",
            style = MaterialTheme.typography.bodyLarge,
            color = Charcoal
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = displayValue,
            onValueChange = { newValue ->
                // Extract only digits from input, max 10 digits
                val digits = newValue.filter { it.isDigit() }.take(10)
                phoneDigits = digits
            },
            placeholder = { Text("(416) 886-3439") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Mustard,
                unfocusedBorderColor = Charcoal,
                cursorColor = Ketchup
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Mustard)
        } else {
            Button(
                onClick = { onSendOtp(phoneWithCountryCode) },
                enabled = phoneDigits.length == 10,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Mustard,
                    contentColor = Charcoal,
                    disabledContainerColor = Mustard.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Send Code",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun OtpVerificationScreen(
    phone: String,
    isLoading: Boolean,
    onVerifyOtp: (String) -> Unit,
    onGoBack: () -> Unit,
    onResendOtp: () -> Unit
) {
    var otp by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onGoBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = Charcoal
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dancing Hotdog (smaller)
        DancingHotdog(
            modifier = Modifier.size(100.dp),
            pixelSize = 4f
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Enter the code",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Charcoal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We sent a code to $phone",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // OTP Input boxes
        OtpInputField(
            otpLength = 6,
            otp = otp,
            onOtpChange = { otp = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Mustard)
        } else {
            Button(
                onClick = { onVerifyOtp(otp) },
                enabled = otp.length == 6,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Mustard,
                    contentColor = Charcoal,
                    disabledContainerColor = Mustard.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Verify",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onResendOtp) {
            Text(
                text = "Resend code",
                color = Ketchup
            )
        }
    }
}

@Composable
private fun OtpInputField(
    otpLength: Int,
    otp: String,
    onOtpChange: (String) -> Unit
) {
    BasicTextField(
        value = otp,
        onValueChange = { value ->
            if (value.length <= otpLength && value.all { it.isDigit() }) {
                onOtpChange(value)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpLength) { index ->
                    val char = otp.getOrNull(index)?.toString() ?: ""
                    val isFocused = otp.length == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(
                                width = 2.dp,
                                color = if (isFocused) Mustard else Charcoal.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Charcoal
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun UserTypeSelectionScreen(
    selectedType: UserType?,
    onSelectUserType: (UserType) -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onGoBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = Charcoal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dancing Hotdog
        DancingHotdog(
            modifier = Modifier.size(120.dp),
            pixelSize = 5f
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Who are you?",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Charcoal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select your role to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // User type cards
        UserType.entries.forEach { userType ->
            UserTypeCard(
                userType = userType,
                isSelected = selectedType == userType,
                onClick = { onSelectUserType(userType) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun UserTypeCard(
    userType: UserType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val description = when (userType) {
        UserType.SHOP -> "Sell goods to customers"
        UserType.SHOPPER -> "Browse and buy goods"
        UserType.PRODUCER -> "Supply goods to shops"
    }

    val emoji = when (userType) {
        UserType.SHOP -> "🏪"
        UserType.SHOPPER -> "🛒"
        UserType.PRODUCER -> "🏭"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Mustard else Color.White)
            .border(
                width = 3.dp,
                color = if (isSelected) Ketchup else Charcoal.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = userType.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Charcoal
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhoneEntryScreenPreview() {
    IndeloGoodsTheme {
        AuthScreen(
            uiState = AuthUiState(),
            onSendOtp = {},
            onVerifyOtp = {},
            onSelectUserType = {},
            onGoBack = {},
            onClearError = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpScreenPreview() {
    IndeloGoodsTheme {
        AuthScreen(
            uiState = AuthUiState(
                step = AuthStep.OTP_VERIFICATION,
                phone = "+1 234 567 8900"
            ),
            onSendOtp = {},
            onVerifyOtp = {},
            onSelectUserType = {},
            onGoBack = {},
            onClearError = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserTypeScreenPreview() {
    IndeloGoodsTheme {
        AuthScreen(
            uiState = AuthUiState(
                step = AuthStep.USER_TYPE_SELECTION
            ),
            onSendOtp = {},
            onVerifyOtp = {},
            onSelectUserType = {},
            onGoBack = {},
            onClearError = {}
        )
    }
}
