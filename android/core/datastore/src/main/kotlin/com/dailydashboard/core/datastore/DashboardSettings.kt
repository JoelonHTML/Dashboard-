package com.dailydashboard.core.datastore

/**
 * [nightStartMinute]/[nightEndMinute] zijn minuten-sinds-middernacht (0..1439),
 * zodat we geen tijdzone-/serialisatie-gedoe met java.time nodig hebben.
 * [manualNightOverride] = null volgt het tijdschema, true/false forceert dag/nacht.
 */
data class DashboardSettings(
    val backendBaseUrl: String = "http://192.168.1.50:4000",
    val pollIntervalSeconds: Int = 30,
    val nightModeEnabled: Boolean = true,
    val nightStartMinute: Int = 22 * 60,
    val nightEndMinute: Int = 7 * 60,
    val manualNightOverride: Boolean? = null,
)
