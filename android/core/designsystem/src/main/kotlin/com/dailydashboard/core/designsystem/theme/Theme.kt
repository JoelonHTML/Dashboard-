package com.dailydashboard.core.designsystem.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class DashboardColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceBorder: Color,
    val accent: Color,
    val alert: Color,
    val textPrimary: Color,
    val textSecondary: Color,
)

private val DayScheme = DashboardColorScheme(
    background = DashboardColors.background,
    surface = DashboardColors.surface,
    surfaceBorder = DashboardColors.surfaceBorder,
    accent = DashboardColors.accent,
    alert = DashboardColors.alert,
    textPrimary = DashboardColors.textPrimary,
    textSecondary = DashboardColors.textSecondary,
)

private val NightScheme = DashboardColorScheme(
    background = DashboardNightColors.background,
    surface = DashboardNightColors.surface,
    surfaceBorder = DashboardNightColors.surfaceBorder,
    accent = DashboardNightColors.accent,
    alert = DashboardNightColors.alert,
    textPrimary = DashboardNightColors.textPrimary,
    textSecondary = DashboardNightColors.textSecondary,
)

val LocalDashboardColors = compositionLocalOf { DayScheme }

/**
 * Nachtmodus is een zachte crossfade tussen [DayScheme] en [NightScheme] — nooit een
 * harde knip. Schermhelderheid zelf wordt in MainActivity via window.attributes geregeld,
 * dat valt buiten wat Compose-kleuren kunnen dimmen.
 */
@Composable
fun DashboardTheme(
    isNight: Boolean,
    accentColorOverride: Color? = null,
    content: @Composable () -> Unit,
) {
    val target = if (isNight) NightScheme else DayScheme
    val targetAccent = accentColorOverride ?: target.accent

    val colors = DashboardColorScheme(
        background = animateColorAsState(target.background, tween(320), label = "background").value,
        surface = animateColorAsState(target.surface, tween(320), label = "surface").value,
        surfaceBorder = animateColorAsState(target.surfaceBorder, tween(320), label = "surfaceBorder").value,
        accent = animateColorAsState(targetAccent, tween(320), label = "accent").value,
        alert = animateColorAsState(target.alert, tween(320), label = "alert").value,
        textPrimary = animateColorAsState(target.textPrimary, tween(320), label = "textPrimary").value,
        textSecondary = animateColorAsState(target.textSecondary, tween(320), label = "textSecondary").value,
    )

    val materialColorScheme = darkColorScheme(
        primary = colors.accent,
        onPrimary = colors.background,
        secondary = colors.accent,
        background = colors.background,
        onBackground = colors.textPrimary,
        surface = colors.surface,
        onSurface = colors.textPrimary,
        onSurfaceVariant = colors.textSecondary,
        error = colors.alert,
        outline = colors.surfaceBorder,
    )

    CompositionLocalProvider(LocalDashboardColors provides colors) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = DashboardTypography,
            content = content,
        )
    }
}

object DashboardTheme {
    val colors: DashboardColorScheme
        @Composable
        get() = LocalDashboardColors.current
}
