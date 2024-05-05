package com.roh.evaluationtask.util

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.roh.evaluationtask.data.local.PostDao
import com.roh.evaluationtask.data.local.PostDatabase
import com.roh.evaluationtask.data.local.PostEntity
import com.roh.evaluationtask.data.local.RemoteKeys
import com.roh.evaluationtask.data.local.RemoteKeysDao
import com.roh.evaluationtask.data.network.ApiService
import com.roh.evaluationtask.domain.model.Post
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "POST_REMOTE_MEDIATOR"

@OptIn(ExperimentalPagingApi::class)
class PostsRemoteMediator(
    private val apiService: ApiService,
    private val database: PostDatabase,
    private val postDao: PostDao,
    private val keysDao: RemoteKeysDao
): RemoteMediator<Int, Post>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Post>
    ): MediatorResult {
        try {
            val currentLoadingPageKey = keysDao.getRemoteKey()?.keyName ?: ""
            Log.d(TAG, "currentLoadingPageKey => $currentLoadingPageKey")

            val response = apiService.getPostsData(currentLoadingPageKey)
            Log.d(TAG, "response => $response")

            val responseData = mutableListOf<Post>()
            responseData.addAll(response.data)
            database.withTransaction {
                postDao.deletePosts()
                // add post to db
                postDao.insertPosts(responseData.map { it.toPostEntity() })

                keysDao.deleteRemoteKeys()
                keysDao.insertKey(
                    RemoteKeys(
                        1,
                        response.next,
                        1 // redundant
                    )
                )
            }

            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (error: IOException) {
            return MediatorResult.Error(error)
        } catch (error: HttpException) {
            return MediatorResult.Error(error)
        }
    }
}