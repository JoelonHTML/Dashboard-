package com.dailydashboard.core.datastore

/**
 * [nightStartMinute]/[nightEndMinute] zijn minuten-sinds-middernacht (0..1439),
 * zodat we geen tijdzone-/serialisatie-gedoe met java.time nodig hebben.
 * [manualNightOverride] = null volgt het tijdschema, true/false forceert dag/nacht.
 * [accentColorHex] = null gebruikt de standaard-accentkleur van het thema; anders "#RRGGBB".
 * [gridColumns] bepaalt hoeveel kolommen de widget-grid heeft (2..6).
 */
data class DashboardSettings(
    val backendBaseUrl: String = "http://192.168.1.43:4000",
    val pollIntervalSeconds: Int = 3,
    val nightModeEnabled: Boolean = true,
    val nightStartMinute: Int = 22 * 60,
    val nightEndMinute: Int = 7 * 60,
    val manualNightOverride: Boolean? = null,
    val accentColorHex: String? = null,
    val gridColumns: Int = 4,
)
