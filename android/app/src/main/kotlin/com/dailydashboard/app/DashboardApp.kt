package com.dailydashboard.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.dailydashboard.app.settings.SettingsSheet
import com.dailydashboard.app.widgetgrid.AddWidgetSheet
import com.dailydashboard.app.widgetgrid.WidgetGrid
import com.dailydashboard.core.designsystem.theme.DashboardTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardApp() {
    val context = LocalContext.current
    val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(context))
    val uiState by viewModel.uiState.collectAsState()
    val connectionTestState by viewModel.connectionTestState.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var hasLoaded by remember { mutableStateOf(false) }

    DashboardTheme(isNight = uiState.isNight) {
        LaunchedEffectOnce { hasLoaded = true }
        DimScreenForNightMode(isNight = uiState.isNight)

        Scaffold(
            containerColor = DashboardTheme.colors.background,
            topBar = {
                TopAppBar(
                    title = { Text("Daily Dashboard") },
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
            )
        }
    }
}

@Composable
private fun LaunchedEffectOnce(block: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit) { block() }
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
