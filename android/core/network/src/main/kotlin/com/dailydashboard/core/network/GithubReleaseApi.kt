package com.dailydashboard.core.network

import com.dailydashboard.core.network.model.GithubReleaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubReleaseApi {
    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): GithubReleaseResponse
}
