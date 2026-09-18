package com.dailydashboard.app.update

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpdateViewModel(
    private val repository: UpdateRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val state: StateFlow<UpdateState> = _state.asStateFlow()

    fun checkForUpdate() {
        viewModelScope.launch {
            _state.value = UpdateState.Checking
            _state.value = runCatching { repository.checkForUpdate() }.fold(
                onSuccess = { info -> if (info != null) UpdateState.Available(info) else UpdateState.UpToDate },
                onFailure = { UpdateState.Error(it.message ?: "Onbekende fout bij het zoeken naar updates") },
            )
        }
    }

    fun downloadAndPrepareInstall(info: UpdateInfo) {
        viewModelScope.launch {
            _state.value = UpdateState.Downloading(info, 0f)
            runCatching {
                repository.downloadApk(info) { progress ->
                    _state.value = UpdateState.Downloading(info, progress)
                }
            }.fold(
                onSuccess = { file -> _state.value = UpdateState.ReadyToInstall(info, file.absolutePath) },
                onFailure = { _state.value = UpdateState.Error(it.message ?: "Downloaden mislukt") },
            )
        }
    }

    fun dismiss() {
        _state.value = UpdateState.Idle
    }

    fun needsInstallPermission(): Boolean = repository.needsInstallPermission()

    fun manageUnknownAppSourcesIntent(): Intent = repository.manageUnknownAppSourcesIntent()

    fun installApkIntent(apkPath: String): Intent = repository.installApkIntent(apkPath)

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UpdateViewModel(repository = UpdateRepository(context.applicationContext))
            }
        }
    }
}
