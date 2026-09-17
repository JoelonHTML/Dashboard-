package com.dailydashboard.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * 1-op-1 uit het referentie-ontwerp. Deze waarden zijn vast — alleen de
 * widget-indeling erboven is aanpasbaar, de stijl niet.
 */
object DashboardColors {
    val background = Color(0xFF0E0E10)
    val surface = Color(0xFF1C1C1F)
    val surfaceBorder = Color(0xFF2A2A2E)
    val accent = Color(0xFF1FCE7C)
    val alert = Color(0xFFF04438)
    val textPrimary = Color(0xFFF5F5F7)
    val textSecondary = Color(0xFF8A8A90)
}

/**
 * Nachtmodus: het dashboard is al donker, dus "nacht" is verder dimmen en
 * een warmere/rossige tint over de bestaande accentkleur — geen apart lichtthema.
 */
object DashboardNightColors {
    val background = Color(0xFF08080A)
    val surface = Color(0xFF17130F)
    val surfaceBorder = Color(0xFF241C15)
    val accent = Color(0xFFE0A24D)
    val alert = Color(0xFFC23B2E)
    val textPrimary = Color(0xFFEDE6DD)
    val textSecondary = Color(0xFF7A6F62)
}
