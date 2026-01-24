package com.indelo.goods.ui.theme

import androidx.compose.ui.graphics.Color

// Retro Hotdog Palette - 4 core colors
val Mustard = Color(0xFFFFD93D)          // Bright mustard yellow
val Ketchup = Color(0xFFFF6B6B)          // Soft red
val Bun = Color(0xFFFFF5E1)              // Cream/off-white
val Charcoal = Color(0xFF2D2D2D)         // Dark charcoal

// Extended palette for UI needs
val MustardDark = Color(0xFFE5C235)
val MustardLight = Color(0xFFFFE566)
val KetchupDark = Color(0xFFE55555)
val KetchupLight = Color(0xFFFF8A8A)

// Background colors
val BackgroundLight = Bun
val BackgroundDark = Color(0xFF1A1A1A)

// Surface colors
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF2D2D2D)

// Primary = Mustard (main brand color)
val Primary = Mustard
val PrimaryLight = MustardLight
val PrimaryDark = MustardDark

// Secondary = Ketchup (accent)
val Secondary = Ketchup
val SecondaryLight = KetchupLight
val SecondaryDark = KetchupDark

// On colors (text/icons on colored backgrounds)
val OnPrimary = Charcoal
val OnSecondary = Color(0xFFFFFFFF)
val OnBackgroundLight = Charcoal
val OnBackgroundDark = Bun
val OnSurfaceLight = Charcoal
val OnSurfaceDark = Bun

// Error colors
val Error = Color(0xFFFF4444)
val ErrorDark = Color(0xFFFF6B6B)
val OnError = Color(0xFFFFFFFF)
