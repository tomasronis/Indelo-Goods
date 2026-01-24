package com.indelo.goods.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.indelo.goods.MainScreen
import com.indelo.goods.ui.auth.AuthScreen
import com.indelo.goods.ui.auth.AuthStep
import com.indelo.goods.ui.auth.AuthViewModel
import io.github.jan.supabase.auth.status.SessionStatus

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Home : Screen("home")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val sessionStatus by authViewModel.sessionStatus.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    val startDestination = when (sessionStatus) {
        is SessionStatus.Authenticated -> Screen.Home.route
        else -> Screen.Auth.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                uiState = authUiState,
                onSendOtp = { phone -> authViewModel.sendOtp(phone) },
                onVerifyOtp = { token -> authViewModel.verifyOtp(token) },
                onSelectUserType = { userType -> authViewModel.selectUserType(userType) },
                onGoBack = { authViewModel.goBack() },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Screen.Home.route) {
            MainScreen(
                onSignOut = { authViewModel.signOut() }
            )
        }
    }

    // Handle navigation based on auth state and user type selection
    LaunchedEffect(sessionStatus, authUiState.selectedUserType) {
        when (sessionStatus) {
            is SessionStatus.Authenticated -> {
                // Only navigate to home if user has selected a type
                if (authUiState.selectedUserType != null) {
                    if (navController.currentDestination?.route == Screen.Auth.route) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                }
            }
            is SessionStatus.NotAuthenticated -> {
                if (navController.currentDestination?.route != Screen.Auth.route) {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> { /* Loading state, do nothing */ }
        }
    }
}
