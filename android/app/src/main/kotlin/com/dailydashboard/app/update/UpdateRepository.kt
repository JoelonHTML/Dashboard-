package com.dailydashboard.app.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import com.dailydashboard.core.network.GithubReleaseApi
import com.dailydashboard.core.network.GithubReleaseApiFactory
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

private const val REPO_OWNER = "JoelonHTML"
private const val REPO_NAME = "Dashboard-"

/**
 * De repo is public, dus de laatste release (en de APK erin, zie android-build.yml) is
 * zonder token op te vragen — geen omweg via de backend nodig.
 */
class UpdateRepository(
    private val context: Context,
    private val api: GithubReleaseApi = GithubReleaseApiFactory.create(),
    private val httpClient: OkHttpClient = OkHttpClient(),
) {
    fun currentVersionCode(): Int {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
    }

    suspend fun checkForUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        val release = api.getLatestRelease(REPO_OWNER, REPO_NAME)
        val remoteVersionCode = parseVersionCode(release.tagName) ?: return@withContext null
        if (remoteVersionCode <= currentVersionCode()) return@withContext null

        val apkAsset = release.assets.firstOrNull { it.name.endsWith(".apk") } ?: return@withContext null

        UpdateInfo(
            versionCode = remoteVersionCode,
            versionLabel = release.name ?: release.tagName,
            downloadUrl = apkAsset.browserDownloadUrl,
            releaseNotes = release.body,
        )
    }

    suspend fun downloadApk(info: UpdateInfo, onProgress: (Float) -> Unit): File = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(info.downloadUrl).build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Download mislukt: HTTP ${response.code}")
            val body = response.body ?: error("Leeg antwoord bij downloaden")

            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val outputFile = File(updatesDir, "daily-dashboard-${info.versionCode}.apk")
            val totalBytes = body.contentLength()
            var readBytes = 0L

            body.byteStream().use { input ->
                outputFile.outputStream().use { output ->
                    val buffer = ByteArray(8 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                        readBytes += read
                        if (totalBytes > 0) onProgress(readBytes.toFloat() / totalBytes)
                    }
                }
            }
            outputFile
        }
    }

    /** true als de gebruiker deze app nog toestemming moet geven om zelf APK's te installeren. */
    fun needsInstallPermission(): Boolean = !context.packageManager.canRequestPackageInstalls()

    fun manageUnknownAppSourcesIntent(): Intent =
        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))

    fun installApkIntent(apkPath: String): Intent {
        val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(apkPath))
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    companion object {
        /** Tag-formaat "apk-build-<run_number>" (zie android-build.yml) — pakt het laatste getal. */
        internal fun parseVersionCode(tagName: String): Int? =
            Regex("(\\d+)$").find(tagName)?.value?.toIntOrNull()
    }
}
