package com.roh.evaluationtask.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.roh.evaluationtask.data.network.ApiService
import com.roh.evaluationtask.domain.model.Post

private const val TAG = "POST_DATA_SOURCE"
class PostDataSource(private val apiService: ApiService) : PagingSource<String, Post>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        try {
            val currentLoadingPageKey = params.key ?: ""
            Log.d(TAG, "currentLoadingPageKey => $currentLoadingPageKey")

            val response = apiService.getPostsData(currentLoadingPageKey)
            Log.d(TAG, "response => $response")

            val responseData = mutableListOf<Post>()
            responseData.addAll(response.data)

            return LoadResult.Page(
                data = responseData,
                prevKey = currentLoadingPageKey,
                nextKey = response.next
            )
        } catch (e: Exception) {
            Log.e(TAG, "failed to load data => $e")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Post>): String? {
        return ""
    }

}