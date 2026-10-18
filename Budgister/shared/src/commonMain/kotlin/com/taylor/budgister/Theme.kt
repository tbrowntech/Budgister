package com.taylor.budgister

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val Ink = Color(0xFF0A0908)
private val Slate = Color(0xFF22333B)
private val Cream = Color(0xFFEAE0D5)
private val Tan = Color(0xFFC6AC8F)
private val Brown = Color(0xFF5E503F)

val BudgisterColors = lightColorScheme(
    // Buttons: slate with cream text
    primary = Slate,
    onPrimary = Cream,
    primaryContainer = Slate,
    onPrimaryContainer = Cream,
    secondary = Slate,
    onSecondary = Cream,
    secondaryContainer = Slate,
    onSecondaryContainer = Cream,
    tertiary = Slate,
    onTertiary = Cream,

    // Page background and all other text
    background = Tan,
    onBackground = Ink,
    surface = Tan,
    onSurface = Ink,
    surfaceVariant = Tan,
    onSurfaceVariant = Ink,

    // Borders
    outline = Brown,
    outlineVariant = Brown,

    // Surfaces
    surfaceContainerLowest = Tan,
    surfaceContainerLow = Tan,
    surfaceContainer = Tan,
    surfaceContainerHigh = Cream,
    surfaceContainerHighest = Tan
)