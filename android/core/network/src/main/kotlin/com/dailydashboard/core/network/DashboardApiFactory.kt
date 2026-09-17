package com.dailydashboard.core.network

import android.content.Context
import com.dailydashboard.core.datastore.DashboardPreferencesDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * De LAN-URL van de backend is instelbaar in de app-instellingen (kan wijzigen
 * zonder herinstallatie), dus Retrofit krijgt een placeholder-baseUrl en deze
 * interceptor herschrijft elk request naar het actueel opgeslagen adres.
 */
private class DynamicBaseUrlInterceptor(
    private val preferences: DashboardPreferencesDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val currentBaseUrl = runBlocking { preferences.settings.first().backendBaseUrl }
        val backendUrl = currentBaseUrl.toHttpUrlOrNull() ?: return chain.proceed(original)

        val newUrl = original.url.newBuilder()
            .scheme(backendUrl.scheme)
            .host(backendUrl.host)
            .port(backendUrl.port)
            .build()

        return chain.proceed(original.newBuilder().url(newUrl).build())
    }
}

object DashboardApiFactory {
    private const val PLACEHOLDER_BASE_URL = "http://localhost/"

    fun create(context: Context): DashboardApi {
        val preferences = DashboardPreferencesDataSource(context.applicationContext)
        val json = Json { ignoreUnknownKeys = true }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(DynamicBaseUrlInterceptor(preferences))
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(PLACEHOLDER_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(DashboardApi::class.java)
    }
}
