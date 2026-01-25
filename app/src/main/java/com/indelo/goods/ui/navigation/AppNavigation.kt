package com.indelo.goods.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.indelo.goods.ui.producer.ProducerOrdersScreen
import com.indelo.goods.ui.producer.ProducerSalesScreen
import com.indelo.goods.ui.producer.ProducerInventoryScreen
import com.indelo.goods.ui.producer.ProducerPayoutsScreen
import com.indelo.goods.ui.producer.ProducerSubscriptionScreen
import com.indelo.goods.ui.public.ProductDetailScreen
import com.indelo.goods.ui.public.ProducerProfileScreen
import com.indelo.goods.ui.shop.OrderViewModel
import com.indelo.goods.ui.shop.OrderScreen
import com.indelo.goods.ui.shop.ProductBrowseViewModel
import com.indelo.goods.ui.shop.ShopCreateScreen
import com.indelo.goods.ui.shop.ShopListScreen
import com.indelo.goods.ui.shop.ShopProductBrowseScreen
import com.indelo.goods.ui.shop.ShopViewModel
import com.indelo.goods.ui.shopper.ShopperHomeScreen
import com.indelo.goods.ui.shopper.ShopperPreferencesScreen
import com.indelo.goods.ui.shopper.ShopperSubscriptionScreen
import com.indelo.goods.ui.shopper.MonthlyProductSelectionScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Home : Screen("home")

    // Producer screens
    data object ProducerHome : Screen("producer/home")
    data object ProducerOrders : Screen("producer/orders")
    data object ProducerSales : Screen("producer/sales")
    data object ProducerInventory : Screen("producer/inventory")
    data object ProducerPayouts : Screen("producer/payouts")
    data object ProducerSubscription : Screen("producer/subscription")
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

    // Shop screens
    data object ShopHome : Screen("shop/home")
    data object ShopCreate : Screen("shop/create")
    data object ShopProductBrowse : Screen("shop/{shopId}/products") {
        fun createRoute(shopId: String) = "shop/$shopId/products"
    }
    data object ShopOrder : Screen("shop/{shopId}/order") {
        fun createRoute(shopId: String) = "shop/$shopId/order"
    }

    // Shopper screens
    data object ShopperHome : Screen("shopper/home")
    data object ShopperPreferences : Screen("shopper/preferences")
    data object ShopperSubscription : Screen("shopper/subscription")
    data object ShopperMonthlyProducts : Screen("shopper/monthly-products")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    deepLinkProductId: String? = null
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
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

    val startDestination = if (isAuthenticated) {
        getHomeRouteForUserType(authUiState.selectedUserType)
    } else {
        Screen.Auth.route
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
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return ProductViewModel(context) as T
                    }
                }
            )

            ProducerHomeScreen(
                onSignOut = { authViewModel.signOut() },
                onCreateProduct = { navController.navigate(Screen.ProductCreate.route) },
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductEdit.createRoute(productId))
                },
                onViewOrders = { navController.navigate(Screen.ProducerOrders.route) },
                onViewSales = { navController.navigate(Screen.ProducerSales.route) },
                onViewInventory = { navController.navigate(Screen.ProducerInventory.route) },
                onViewPayouts = { navController.navigate(Screen.ProducerPayouts.route) },
                viewModel = productViewModel
            )
        }

        composable(Screen.ProducerOrders.route) {
            ProducerOrdersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProducerSales.route) {
            ProducerSalesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProducerInventory.route) {
            ProducerInventoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProducerPayouts.route) {
            ProducerPayoutsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProducerSubscription.route) {
            ProducerSubscriptionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProductCreate.route) {
            val context = LocalContext.current
            val productViewModel: ProductViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return ProductViewModel(context) as T
                    }
                }
            )
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
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return ProductViewModel(context) as T
                    }
                }
            )

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

        // Shop screens
        composable(Screen.ShopHome.route) {
            val shopViewModel: ShopViewModel = viewModel()

            ShopListScreen(
                onSignOut = { authViewModel.signOut() },
                onCreateShop = { navController.navigate(Screen.ShopCreate.route) },
                onShopClick = { shopId ->
                    navController.navigate(Screen.ShopProductBrowse.createRoute(shopId))
                },
                viewModel = shopViewModel
            )
        }

        composable(Screen.ShopCreate.route) {
            val shopViewModel: ShopViewModel = viewModel()
            val formState by shopViewModel.formState.collectAsState()

            LaunchedEffect(formState.isSuccess) {
                if (formState.isSuccess) {
                    shopViewModel.clearFormState()
                    navController.popBackStack()
                }
            }

            ShopCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onShopCreated = { formData ->
                    shopViewModel.createShopFromForm(formData)
                },
                isLoading = formState.isLoading
            )
        }

        composable(
            route = Screen.ShopProductBrowse.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: return@composable
            val productBrowseViewModel: ProductBrowseViewModel = viewModel()
            val orderViewModel: OrderViewModel = viewModel()

            ShopProductBrowseScreen(
                shopId = shopId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = {
                    navController.navigate(Screen.ShopOrder.createRoute(shopId))
                },
                onAddToCart = { product ->
                    orderViewModel.addProduct(product)
                },
                viewModel = productBrowseViewModel
            )
        }

        composable(
            route = Screen.ShopOrder.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: return@composable
            val orderViewModel: OrderViewModel = viewModel()

            OrderScreen(
                shopId = shopId,
                onNavigateBack = { navController.popBackStack() },
                onOrderPlaced = {
                    // Navigate back to shop list after successful order
                    navController.navigate(Screen.ShopHome.route) {
                        popUpTo(Screen.ShopHome.route) { inclusive = true }
                    }
                },
                viewModel = orderViewModel
            )
        }

        // Shopper screens
        composable(Screen.ShopperHome.route) {
            ShopperHomeScreen(
                onSignOut = { authViewModel.signOut() },
                onViewPreferences = { navController.navigate(Screen.ShopperPreferences.route) },
                onViewSubscription = { navController.navigate(Screen.ShopperSubscription.route) },
                onSelectMonthlyProducts = { navController.navigate(Screen.ShopperMonthlyProducts.route) },
                hasActiveSubscription = false, // TODO: Get from ViewModel
                monthlyProductsSelected = 0 // TODO: Get from ViewModel
            )
        }

        composable(Screen.ShopperPreferences.route) {
            ShopperPreferencesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ShopperSubscription.route) {
            ShopperSubscriptionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ShopperMonthlyProducts.route) {
            MonthlyProductSelectionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }

    // Handle navigation based on auth state and user type selection
    LaunchedEffect(isAuthenticated, authUiState.selectedUserType) {
        if (isAuthenticated) {
            // Only navigate if user has selected a type
            authUiState.selectedUserType?.let { userType ->
                if (navController.currentDestination?.route == Screen.Auth.route) {
                    val route = getHomeRouteForUserType(userType)
                    navController.navigate(route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            }
        } else {
            // Not authenticated - navigate to auth screen
            if (navController.currentDestination?.route != Screen.Auth.route) {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
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
