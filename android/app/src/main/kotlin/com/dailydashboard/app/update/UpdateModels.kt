package com.dailydashboard.app.update

data class UpdateInfo(
    val versionCode: Int,
    val versionLabel: String,
    val downloadUrl: String,
    val releaseNotes: String?,
)

sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data object UpToDate : UpdateState
    data class Available(val info: UpdateInfo) : UpdateState
    data class Downloading(val info: UpdateInfo, val progress: Float) : UpdateState
    data class ReadyToInstall(val info: UpdateInfo, val apkPath: String) : UpdateState
    data class Error(val message: String) : UpdateState
}
