package com.indelo.goods

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.indelo.goods.ui.navigation.AppNavigation
import com.indelo.goods.ui.theme.IndeloGoodsTheme

class MainActivity : ComponentActivity() {

    private var deepLinkProductId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle deep link
        handleDeepLink(intent)

        setContent {
            IndeloGoodsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        deepLinkProductId = deepLinkProductId
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            // Extract product ID from deep link
            // Format: indelogoods://product/{productId} or https://indelogoods.com/product/{productId}
            val productId = data.pathSegments.lastOrNull()
            deepLinkProductId = productId
        }
    }
}
