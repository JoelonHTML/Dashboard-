package com.dailydashboard.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.dailydashboard.app.settings.SettingsSheet
import com.dailydashboard.app.update.UpdateBanner
import com.dailydashboard.app.update.UpdateViewModel
import com.dailydashboard.app.widgetgrid.AddWidgetSheet
import com.dailydashboard.app.widgetgrid.WidgetGrid
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import com.dailydashboard.core.ui.clock.rememberCurrentDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTimeTextStyle
import java.util.Locale

private val topBarTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val topBarDateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale("nl"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardApp() {
    val context = LocalContext.current
    val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(context))
    val uiState by viewModel.uiState.collectAsState()
    val connectionTestState by viewModel.connectionTestState.collectAsState()

    val updateViewModel: UpdateViewModel = viewModel(factory = UpdateViewModel.factory(context))
    val updateState by updateViewModel.state.collectAsState()
    val onInstallUpdate: (String) -> Unit = { apkPath ->
        val intent = if (updateViewModel.needsInstallPermission()) {
            updateViewModel.manageUnknownAppSourcesIntent()
        } else {
            updateViewModel.installApkIntent(apkPath)
        }
        context.startActivity(intent)
    }

    var isEditMode by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var hasLoaded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val accentColorOverride = remember(uiState.settings.accentColorHex) {
        uiState.settings.accentColorHex?.let { hex ->
            runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex)) }.getOrNull()
        }
    }

    DashboardTheme(isNight = uiState.isNight, accentColorOverride = accentColorOverride) {
        LaunchedEffectOnce { hasLoaded = true }
        LaunchedEffectOnce { updateViewModel.checkForUpdate() }
        DimScreenForNightMode(isNight = uiState.isNight)

        Scaffold(
            containerColor = DashboardTheme.colors.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column {
                    TopAppBar(
                        title = { LiveClockTitle() },
                        actions = {
                            androidx.compose.material3.IconButton(onClick = { showSettingsSheet = true }) {
                                Icon(Icons.Filled.Settings, contentDescription = "Instellingen")
                            }
                            androidx.compose.material3.IconButton(onClick = { isEditMode = !isEditMode }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Bewerkmodus")
                            }
                        },
                        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                            containerColor = DashboardTheme.colors.background,
                            titleContentColor = DashboardTheme.colors.textPrimary,
                        ),
                    )
                    UpdateBanner(
                        state = updateState,
                        onDownload = { info -> updateViewModel.downloadAndPrepareInstall(info) },
                        onInstall = onInstallUpdate,
                        onDismiss = { updateViewModel.dismiss() },
                    )
                }
            },
            floatingActionButton = {
                if (isEditMode) {
                    FloatingActionButton(onClick = { showAddSheet = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Widget toevoegen")
                    }
                }
            },
        ) { innerPadding ->
            AnimatedVisibility(
                visible = hasLoaded,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { it / 8 },
                ),
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    WidgetGrid(
                        widgets = uiState.widgets,
                        isEditMode = isEditMode,
                        onReorder = viewModel::reorder,
                        onRemove = { viewModel.removeWidget(it.id) },
                        onCycleSize = { viewModel.cycleWidgetSize(it.id) },
                        modifier = Modifier.fillMaxSize(),
                        gridColumns = uiState.settings.gridColumns,
                    )
                }
            }
        }

        if (showAddSheet) {
            AddWidgetSheet(
                onDismiss = { showAddSheet = false },
                onWidgetChosen = { dataSource ->
                    viewModel.addWidget(dataSource)
                    showAddSheet = false
                    coroutineScope.launch { snackbarHostState.showSnackbar("Widget toegevoegd") }
                },
            )
        }

        if (showSettingsSheet) {
            SettingsSheet(
                settings = uiState.settings,
                connectionTestState = connectionTestState,
                onDismiss = { showSettingsSheet = false },
                onBackendUrlChanged = viewModel::updateBackendBaseUrl,
                onTestConnection = viewModel::testConnection,
                onPollIntervalChanged = viewModel::updatePollIntervalSeconds,
                onNightModeEnabledChanged = viewModel::updateNightModeEnabled,
                onNightWindowChanged = viewModel::updateNightWindow,
                onManualOverrideChanged = viewModel::setManualNightOverride,
                onResetLayout = viewModel::resetToDefaultLayout,
                onAccentColorChanged = viewModel::updateAccentColor,
                onGridColumnsChanged = viewModel::updateGridColumns,
                updateState = updateState,
                onCheckForUpdate = { updateViewModel.checkForUpdate() },
                onDownloadUpdate = { info -> updateViewModel.downloadAndPrepareInstall(info) },
                onInstallUpdate = onInstallUpdate,
            )
        }
    }
}

@Composable
private fun LaunchedEffectOnce(block: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit) { block() }
}

/** Vaste klok in de top-bar — clean lettertype (Montserrat), elke seconde bijgewerkt. */
@Composable
private fun LiveClockTitle() {
    val now = rememberCurrentDateTime()
    val dayName = now.dayOfWeek.getDisplayName(JavaTimeTextStyle.FULL, Locale("nl"))
        .replaceFirstChar { it.uppercase() }

    Column {
        Text(
            text = now.format(topBarTimeFormatter),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "$dayName ${now.format(topBarDateFormatter)}",
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

/** Zachte crossfade van schermhelderheid bij het in-/uitschakelen van nachtmodus, geen harde knip. */
@Composable
private fun DimScreenForNightMode(isNight: Boolean) {
    val activity = LocalContext.current as? ComponentActivity ?: return
    androidx.compose.runtime.LaunchedEffect(isNight) {
        val targetBrightness = if (isNight) 0.12f else 1f
        val startBrightness = activity.window.attributes.screenBrightness.let { if (it < 0f) 1f else it }
        val steps = 20
        repeat(steps) { step ->
            val fraction = (step + 1f) / steps
            val brightness = startBrightness + (targetBrightness - startBrightness) * fraction
            activity.window.attributes = activity.window.attributes.apply {
                screenBrightness = brightness.coerceIn(0.05f, 1f)
            }
            delay(16L)
        }
    }
}
