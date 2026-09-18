package com.dailydashboard.core.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Vaste, publieke GitHub-API — anders dan [DashboardApiFactory] geen instelbaar adres nodig
 * en geen authenticatie (de repo is public, dus releases zijn zonder token op te vragen).
 */
object GithubReleaseApiFactory {
    private const val BASE_URL = "https://api.github.com/"

    fun create(): GithubReleaseApi {
        val json = Json { ignoreUnknownKeys = true }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(GithubReleaseApi::class.java)
    }
}
