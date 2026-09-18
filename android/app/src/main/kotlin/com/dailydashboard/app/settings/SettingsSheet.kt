package com.dailydashboard.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dailydashboard.app.ConnectionTestState
import com.dailydashboard.app.update.UpdateInfo
import com.dailydashboard.app.update.UpdateState
import com.dailydashboard.core.datastore.DashboardSettings
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** Presetkleuren voor de accentkiezer — geen vrije kleurkiezer nodig voor een tablet-dashboard. */
private val accentColorPresets = listOf(
    "#4C8DFF" to Color(0xFF4C8DFF),
    "#34C77B" to Color(0xFF34C77B),
    "#FF9F43" to Color(0xFFFF9F43),
    "#FF5C7A" to Color(0xFFFF5C7A),
    "#B27CFF" to Color(0xFFB27CFF),
    "#2DD4E0" to Color(0xFF2DD4E0),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: DashboardSettings,
    connectionTestState: ConnectionTestState,
    onDismiss: () -> Unit,
    onBackendUrlChanged: (String) -> Unit,
    onTestConnection: () -> Unit,
    onPollIntervalChanged: (Int) -> Unit,
    onNightModeEnabledChanged: (Boolean) -> Unit,
    onNightWindowChanged: (startMinute: Int, endMinute: Int) -> Unit,
    onManualOverrideChanged: (Boolean?) -> Unit,
    onResetLayout: () -> Unit,
    onAccentColorChanged: (String?) -> Unit,
    onGridColumnsChanged: (Int) -> Unit,
    updateState: UpdateState,
    onCheckForUpdate: () -> Unit,
    onDownloadUpdate: (UpdateInfo) -> Unit,
    onInstallUpdate: (apkPath: String) -> Unit,
) {
    var backendUrl by remember(settings.backendBaseUrl) { mutableStateOf(settings.backendBaseUrl) }
    var startText by remember(settings.nightStartMinute) { mutableStateOf(minuteToText(settings.nightStartMinute)) }
    var endText by remember(settings.nightEndMinute) { mutableStateOf(minuteToText(settings.nightEndMinute)) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Instellingen", style = MaterialTheme.typography.titleMedium)

            UpdateSection(
                state = updateState,
                onCheckForUpdate = onCheckForUpdate,
                onDownloadUpdate = onDownloadUpdate,
                onInstallUpdate = onInstallUpdate,
            )

            HorizontalDivider()

            OutlinedTextField(
                value = backendUrl,
                onValueChange = { backendUrl = it },
                label = { Text("Backend-adres (http://laptop-ip:4000)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { onBackendUrlChanged(backendUrl) }, modifier = Modifier.weight(1f)) {
                    Text("Adres opslaan")
                }
                OutlinedButton(onClick = onTestConnection, modifier = Modifier.weight(1f)) {
                    Text("Test verbinding")
                }
            }
            ConnectionTestStatusLine(connectionTestState)

            HorizontalDivider()

            Text(text = "Ververssnelheid: ${settings.pollIntervalSeconds}s", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = settings.pollIntervalSeconds.toFloat(),
                onValueChange = { onPollIntervalChanged(it.toInt()) },
                valueRange = 1f..30f,
                steps = 28,
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Nachtmodus (tijdgebaseerd)", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = settings.nightModeEnabled, onCheckedChange = onNightModeEnabledChanged)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = startText,
                    onValueChange = { startText = it },
                    label = { Text("Start") },
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = endText,
                    onValueChange = { endText = it },
                    label = { Text("Einde") },
                    modifier = Modifier.weight(1f),
                )
            }
            OutlinedButton(
                onClick = {
                    val start = textToMinuteOrNull(startText)
                    val end = textToMinuteOrNull(endText)
                    if (start != null && end != null) onNightWindowChanged(start, end)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Tijdvak opslaan")
            }

            Text(text = "Handmatige override", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onManualOverrideChanged(null) }) { Text("Automatisch") }
                OutlinedButton(onClick = { onManualOverrideChanged(false) }) { Text("Altijd dag") }
                OutlinedButton(onClick = { onManualOverrideChanged(true) }) { Text("Altijd nacht") }
            }

            HorizontalDivider()

            Text(text = "Accentkleur", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccentSwatch(
                    color = DashboardTheme.colors.accent,
                    isSelected = settings.accentColorHex == null,
                    onClick = { onAccentColorChanged(null) },
                )
                accentColorPresets.forEach { (hex, color) ->
                    AccentSwatch(
                        color = color,
                        isSelected = settings.accentColorHex == hex,
                        onClick = { onAccentColorChanged(hex) },
                    )
                }
            }

            HorizontalDivider()

            Text(text = "Aantal kolommen: ${settings.gridColumns}", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = settings.gridColumns.toFloat(),
                onValueChange = { onGridColumnsChanged(it.toInt()) },
                valueRange = 2f..6f,
                steps = 3,
            )

            HorizontalDivider()

            Text(text = "Widget-lay-out", style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = onResetLayout, modifier = Modifier.fillMaxWidth()) {
                Text("Herstel standaardindeling")
            }
        }
    }
}

@Composable
private fun AccentSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = DashboardTheme.colors
    Row(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = colors.textPrimary,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
    ) {}
}

@Composable
private fun UpdateSection(
    state: UpdateState,
    onCheckForUpdate: () -> Unit,
    onDownloadUpdate: (UpdateInfo) -> Unit,
    onInstallUpdate: (apkPath: String) -> Unit,
) {
    val colors = DashboardTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Software-update", style = MaterialTheme.typography.bodyMedium)

        when (state) {
            UpdateState.Idle, UpdateState.UpToDate -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (state == UpdateState.UpToDate) "Je hebt de laatste versie" else "Nog niet gecontroleerd",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
                OutlinedButton(onClick = onCheckForUpdate) { Text("Zoek naar updates") }
            }

            UpdateState.Checking -> Text(
                text = "Controleren op updates…",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )

            is UpdateState.Available -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Update ${state.info.versionLabel} beschikbaar", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = { onDownloadUpdate(state.info) }) { Text("Downloaden") }
            }

            is UpdateState.Downloading -> Text(
                text = "Downloaden… ${(state.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )

            is UpdateState.ReadyToInstall -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Update ${state.info.versionLabel} gedownload", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = { onInstallUpdate(state.apkPath) }) { Text("Installeren") }
            }

            is UpdateState.Error -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Fout: ${state.message}", style = MaterialTheme.typography.bodyMedium, color = colors.alert)
                OutlinedButton(onClick = onCheckForUpdate) { Text("Opnieuw proberen") }
            }
        }
    }
}

@Composable
private fun ConnectionTestStatusLine(state: ConnectionTestState) {
    val colors = DashboardTheme.colors
    val (text, color) = when (state) {
        is ConnectionTestState.Idle -> return
        is ConnectionTestState.Testing -> "Verbinden…" to colors.textSecondary
        is ConnectionTestState.Success -> "✓ Verbonden met de backend" to colors.accent
        is ConnectionTestState.Failure -> "✗ Niet bereikbaar — ${state.message}" to colors.alert
    }
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = color)
}

private fun minuteToText(minuteOfDay: Int): String =
    LocalTime.of(minuteOfDay / 60, minuteOfDay % 60).format(timeFormatter)

private fun textToMinuteOrNull(text: String): Int? =
    runCatching { LocalTime.parse(text, timeFormatter) }.getOrNull()?.let { it.hour * 60 + it.minute }
