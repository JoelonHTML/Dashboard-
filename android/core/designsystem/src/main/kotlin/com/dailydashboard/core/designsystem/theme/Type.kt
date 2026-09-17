package com.dailydashboard.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dailydashboard.core.designsystem.R

/**
 * Montserrat (OFL-licentie, meegeleverd als variabel lettertype in res/font,
 * licentietekst in core:designsystem/licenses) — geometrische sans-serif, vet voor
 * grote cijfers, regular/gedempt voor labels. Compose synthetiseert bold/semibold
 * vanuit dit ene regular-instance via de standaard fontSynthesis.
 */
val DashboardFontFamily = FontFamily(Font(R.font.montserrat_variable))

val DashboardTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = DashboardFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 44.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = DashboardFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = DashboardFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = DashboardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = DashboardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = DashboardFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)
