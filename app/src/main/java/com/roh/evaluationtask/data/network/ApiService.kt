package com.roh.evaluationtask.data.network

import com.roh.evaluationtask.domain.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/feed/{next}")
    suspend fun getPostsData(@Path("next") nextPageKey: String): ApiResponse

}