package com.indelo.goods.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.indelo.goods.ui.theme.Charcoal
import com.indelo.goods.ui.theme.Ketchup
import com.indelo.goods.ui.theme.Mustard

// Pixel art colors
private val BunColor = Color(0xFFD4A574)
private val BunLight = Color(0xFFE8C49A)
private val HotdogColor = Color(0xFFC65D3B)
private val HotdogDark = Color(0xFFA04830)
private val MustardColor = Mustard
private val KetchupColor = Ketchup
private val SkinColor = Color(0xFFFFDBB4)
private val GloveColor = Color(0xFFFFFFFF)
private val ShoeColor = Charcoal

@Composable
fun DancingHotdog(
    modifier: Modifier = Modifier,
    pixelSize: Float = 4f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hotdog_dance")

    // Dance frame animation (0 to 3 for 4 frames)
    val frame by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dance_frame"
    )

    // Bounce animation
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Canvas(modifier = modifier) {
        val p = pixelSize
        val currentFrame = frame.toInt() % 4
        val bounceOffset = bounce * p * 2

        // Helper to draw a pixel
        fun pixel(x: Int, y: Int, color: Color, yOffset: Float = 0f) {
            drawRect(
                color = color,
                topLeft = Offset(x * p, y * p + yOffset),
                size = Size(p, p)
            )
        }

        // Helper to draw multiple pixels
        fun pixels(coords: List<Pair<Int, Int>>, color: Color, yOffset: Float = 0f) {
            coords.forEach { (x, y) -> pixel(x, y, color, yOffset) }
        }

        val bodyOffset = -bounceOffset

        // Arm positions based on frame
        val leftArmUp = currentFrame == 0 || currentFrame == 2
        val rightArmUp = currentFrame == 1 || currentFrame == 3

        // Leg positions based on frame
        val leftLegOut = currentFrame == 0 || currentFrame == 1
        val rightLegOut = currentFrame == 2 || currentFrame == 3

        // === HOTDOG BUN (body) ===
        // Top bun curve
        pixels(listOf(8 to 4, 9 to 4, 10 to 4, 11 to 4, 12 to 4, 13 to 4), BunLight, bodyOffset)
        pixels(listOf(7 to 5, 14 to 5), BunLight, bodyOffset)
        pixels(listOf(8 to 5, 9 to 5, 10 to 5, 11 to 5, 12 to 5, 13 to 5), BunColor, bodyOffset)

        // Main bun body
        for (row in 6..14) {
            pixel(6, row, BunLight, bodyOffset)
            pixel(7, row, BunColor, bodyOffset)
            for (col in 8..13) {
                pixel(col, row, BunColor, bodyOffset)
            }
            pixel(14, row, BunColor, bodyOffset)
            pixel(15, row, BunLight, bodyOffset)
        }

        // Bottom bun curve
        pixels(listOf(7 to 15, 14 to 15), BunLight, bodyOffset)
        pixels(listOf(8 to 15, 9 to 15, 10 to 15, 11 to 15, 12 to 15, 13 to 15), BunColor, bodyOffset)
        pixels(listOf(8 to 16, 9 to 16, 10 to 16, 11 to 16, 12 to 16, 13 to 16), BunLight, bodyOffset)

        // === HOTDOG (sausage) peeking out ===
        pixels(listOf(7 to 7, 7 to 8, 7 to 9, 7 to 10, 7 to 11, 7 to 12, 7 to 13), HotdogColor, bodyOffset)
        pixels(listOf(14 to 7, 14 to 8, 14 to 9, 14 to 10, 14 to 11, 14 to 12, 14 to 13), HotdogColor, bodyOffset)
        pixel(6, 8, HotdogDark, bodyOffset)
        pixel(6, 12, HotdogDark, bodyOffset)
        pixel(15, 8, HotdogDark, bodyOffset)
        pixel(15, 12, HotdogDark, bodyOffset)

        // === MUSTARD ZIGZAG ===
        pixels(listOf(8 to 8, 9 to 9, 10 to 8, 11 to 9, 12 to 8, 13 to 9), MustardColor, bodyOffset)

        // === KETCHUP LINE ===
        pixels(listOf(8 to 11, 9 to 11, 10 to 11, 11 to 11, 12 to 11, 13 to 11), KetchupColor, bodyOffset)

        // === FACE ===
        // Eyes
        pixel(9, 6, Charcoal, bodyOffset)
        pixel(12, 6, Charcoal, bodyOffset)
        // Smile
        pixels(listOf(9 to 7, 10 to 8, 11 to 8, 12 to 7), Charcoal, bodyOffset)

        // === ARMS ===
        // Left arm
        if (leftArmUp) {
            pixels(listOf(4 to 6, 5 to 7, 5 to 8), SkinColor, bodyOffset)
            pixels(listOf(3 to 5, 4 to 5), GloveColor, bodyOffset)
        } else {
            pixels(listOf(4 to 10, 5 to 9, 5 to 10), SkinColor, bodyOffset)
            pixels(listOf(3 to 11, 4 to 11), GloveColor, bodyOffset)
        }

        // Right arm
        if (rightArmUp) {
            pixels(listOf(16 to 7, 16 to 8, 17 to 6), SkinColor, bodyOffset)
            pixels(listOf(17 to 5, 18 to 5), GloveColor, bodyOffset)
        } else {
            pixels(listOf(16 to 9, 16 to 10, 17 to 10), SkinColor, bodyOffset)
            pixels(listOf(17 to 11, 18 to 11), GloveColor, bodyOffset)
        }

        // === LEGS ===
        val legY = 17
        // Left leg
        if (leftLegOut) {
            pixels(listOf(8 to legY, 7 to 18, 6 to 19), SkinColor, 0f)
            pixels(listOf(5 to 20, 6 to 20), ShoeColor, 0f)
        } else {
            pixels(listOf(9 to legY, 9 to 18, 9 to 19), SkinColor, 0f)
            pixels(listOf(8 to 20, 9 to 20), ShoeColor, 0f)
        }

        // Right leg
        if (rightLegOut) {
            pixels(listOf(13 to legY, 14 to 18, 15 to 19), SkinColor, 0f)
            pixels(listOf(15 to 20, 16 to 20), ShoeColor, 0f)
        } else {
            pixels(listOf(12 to legY, 12 to 18, 12 to 19), SkinColor, 0f)
            pixels(listOf(12 to 20, 13 to 20), ShoeColor, 0f)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF5E1)
@Composable
fun DancingHotdogPreview() {
    DancingHotdog(
        modifier = Modifier.size(120.dp),
        pixelSize = 5f
    )
}
