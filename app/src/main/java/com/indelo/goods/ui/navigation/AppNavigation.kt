package com.indelo.goods.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.indelo.goods.MainScreen
import com.indelo.goods.data.model.UserType
import com.indelo.goods.ui.auth.AuthScreen
import com.indelo.goods.ui.auth.AuthViewModel
import com.indelo.goods.ui.producer.ProductCreateScreen
import com.indelo.goods.ui.cart.CartViewModel
import com.indelo.goods.ui.cart.CheckoutScreen
import com.indelo.goods.ui.producer.ProductEditScreen
import com.indelo.goods.ui.producer.ProductFormState
import com.indelo.goods.ui.producer.ProductViewModel
import com.indelo.goods.ui.producer.ProducerHomeScreen
import com.indelo.goods.ui.public.ProductDetailScreen
import com.indelo.goods.ui.public.ProducerProfileScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.jan.supabase.auth.status.SessionStatus

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Home : Screen("home")

    // Producer screens
    data object ProducerHome : Screen("producer/home")
    data object ProductCreate : Screen("producer/product/create")
    data object ProductEdit : Screen("producer/product/edit/{productId}") {
        fun createRoute(productId: String) = "producer/product/edit/$productId"
    }

    // Public screens (no auth required)
    data object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    data object ProducerProfile : Screen("producer/{producerId}") {
        fun createRoute(producerId: String) = "producer/$producerId"
    }

    // Shopping
    data object Checkout : Screen("checkout")

    // Shop screens (placeholder)
    data object ShopHome : Screen("shop/home")

    // Shopper screens (placeholder)
    data object ShopperHome : Screen("shopper/home")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    deepLinkProductId: String? = null
) {
    val sessionStatus by authViewModel.sessionStatus.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    // Handle deep link navigation
    LaunchedEffect(deepLinkProductId) {
        if (deepLinkProductId != null) {
            navController.navigate(Screen.ProductDetail.createRoute(deepLinkProductId)) {
                // Clear back stack to prevent going back to auth
                popUpTo(0) { inclusive = false }
            }
        }
    }

    val startDestination = when (sessionStatus) {
        is SessionStatus.Authenticated -> getHomeRouteForUserType(authUiState.selectedUserType)
        else -> Screen.Auth.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth
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

        // Generic home (redirects based on user type)
        composable(Screen.Home.route) {
            // Redirect to appropriate home based on user type
            LaunchedEffect(authUiState.selectedUserType) {
                authUiState.selectedUserType?.let { userType ->
                    val route = getHomeRouteForUserType(userType)
                    if (route != Screen.Home.route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }
            }

            // Fallback while redirecting
            MainScreen(onSignOut = { authViewModel.signOut() })
        }

        // Producer screens
        composable(Screen.ProducerHome.route) {
            val context = LocalContext.current
            val productViewModel: ProductViewModel = viewModel(
                factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as android.app.Application
                )
            ) {
                ProductViewModel(context)
            }

            ProducerHomeScreen(
                onSignOut = { authViewModel.signOut() },
                onCreateProduct = { navController.navigate(Screen.ProductCreate.route) },
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductEdit.createRoute(productId))
                },
                viewModel = productViewModel
            )
        }

        composable(Screen.ProductCreate.route) {
            val context = LocalContext.current
            val productViewModel: ProductViewModel = viewModel(
                factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as android.app.Application
                )
            ) {
                ProductViewModel(context)
            }
            val createState by productViewModel.createState.collectAsState()

            LaunchedEffect(createState.isSuccess) {
                if (createState.isSuccess) {
                    productViewModel.clearCreateState()
                    navController.popBackStack()
                }
            }

            ProductCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onProductCreated = { formState, imageUri ->
                    productViewModel.createProduct(formState, imageUri)
                },
                isLoading = createState.isLoading
            )
        }

        composable(
            route = Screen.ProductEdit.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val context = LocalContext.current
            val productViewModel: ProductViewModel = viewModel(
                factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as android.app.Application
                )
            ) {
                ProductViewModel(context)
            }

            ProductEditScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = productViewModel
            )
        }

        // Public Product Detail (no auth required)
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val cartViewModel: CartViewModel = viewModel()

            ProductDetailScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProducer = { producerId ->
                    navController.navigate(Screen.ProducerProfile.createRoute(producerId))
                },
                onAddToCart = { product ->
                    cartViewModel.addToCart(product)
                    navController.navigate(Screen.Checkout.route)
                }
            )
        }

        // Public Producer Profile (no auth required)
        composable(
            route = Screen.ProducerProfile.route,
            arguments = listOf(navArgument("producerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val producerId = backStackEntry.arguments?.getString("producerId") ?: return@composable

            ProducerProfileScreen(
                producerId = producerId,
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        // Checkout / Shopping Cart
        composable(Screen.Checkout.route) {
            val cartViewModel: CartViewModel = viewModel()

            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onCheckout = {
                    // TODO: Implement Stripe checkout
                    // For now, just show a placeholder
                },
                viewModel = cartViewModel
            )
        }

        // Shop screens (placeholder)
        composable(Screen.ShopHome.route) {
            MainScreen(
                onSignOut = { authViewModel.signOut() }
            )
        }

        // Shopper screens (placeholder)
        composable(Screen.ShopperHome.route) {
            MainScreen(
                onSignOut = { authViewModel.signOut() }
            )
        }
    }

    // Handle navigation based on auth state and user type selection
    LaunchedEffect(sessionStatus, authUiState.selectedUserType) {
        when (sessionStatus) {
            is SessionStatus.Authenticated -> {
                // Only navigate if user has selected a type
                authUiState.selectedUserType?.let { userType ->
                    if (navController.currentDestination?.route == Screen.Auth.route) {
                        val route = getHomeRouteForUserType(userType)
                        navController.navigate(route) {
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

private fun getHomeRouteForUserType(userType: UserType?): String {
    return when (userType) {
        UserType.PRODUCER -> Screen.ProducerHome.route
        UserType.SHOP -> Screen.ShopHome.route
        UserType.SHOPPER -> Screen.ShopperHome.route
        null -> Screen.Home.route
    }
}
