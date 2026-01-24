package com.indelo.goods.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QrCodeGenerator {

    /**
     * Generates a QR code bitmap from the given content
     * @param content The content to encode in the QR code
     * @param size The size of the QR code in pixels (both width and height)
     * @return A Bitmap containing the QR code
     */
    fun generateQrCode(content: String, size: Int = 512): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }

    /**
     * Generates a deep link URL for a product
     * @param productId The ID of the product
     * @return The deep link URL that will open the product in the app
     */
    fun generateProductDeepLink(productId: String): String {
        return "indelogoods://product/$productId"
    }

    /**
     * Generates a web URL for a product (for future web integration)
     * @param productId The ID of the product
     * @return The web URL for the product
     */
    fun generateProductWebUrl(productId: String): String {
        return "https://indelogoods.com/product/$productId"
    }
}
