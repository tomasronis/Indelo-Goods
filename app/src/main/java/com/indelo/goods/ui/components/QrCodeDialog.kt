package com.indelo.goods.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard
import com.indelo.goods.util.QrCodeGenerator

@Composable
fun QrCodeDialog(
    productId: String,
    productName: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val qrCodeBitmap = remember(productId) {
        QrCodeGenerator.generateQrCode(
            QrCodeGenerator.generateProductDeepLink(productId),
            size = 512
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dancing hotdog easter egg!
                DancingHotdog(
                    modifier = Modifier.size(48.dp),
                    pixelSize = 3f
                )

                Text(
                    text = "QR Code",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )

                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Charcoal.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                // QR Code with retro border
                Card(
                    colors = CardDefaults.cardColors(containerColor = Mustard.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Image(
                        bitmap = qrCodeBitmap.asImageBitmap(),
                        contentDescription = "QR Code for $productName",
                        modifier = Modifier
                            .size(280.dp)
                            .padding(16.dp)
                    )
                }

                Text(
                    text = "Shoppers can scan this code to view your product!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Close",
                        color = Ketchup,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
