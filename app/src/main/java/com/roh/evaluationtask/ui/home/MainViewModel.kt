package com.roh.evaluationtask.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.roh.evaluationtask.data.local.PostDao
import com.roh.evaluationtask.data.local.PostDatabase
import com.roh.evaluationtask.data.local.RemoteKeysDao
import com.roh.evaluationtask.data.network.ApiService
import com.roh.evaluationtask.domain.model.Post
import com.roh.evaluationtask.util.PostsRemoteMediator
import com.roh.evaluationtask.util.toPost
import com.roh.evaluationtask.util.toPostEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MAIN_VIEW_MODEL"

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val apiService: ApiService,
    private val postDatabase: PostDatabase,
    private val postDao: PostDao,
    private val keysDao: RemoteKeysDao,
) : ViewModel() {

    private var nexPageKey = ""
    private var isReachedLast = false
    var isLoading = false

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>>
        get() = _posts.asStateFlow()

    init {
        updatePostFromLocal()
    }


    private fun updatePostFromLocal() {
        viewModelScope.launch {
            // Before api call look for local db
            val localPosts = postDao.getPosts().first()
            _posts.emit(localPosts.map { it.toPost() })

            // call api to get latest data
            fetchPosts(true)
        }
    }

    fun fetchPosts(isInitialCall: Boolean = false) { // need to handle race condition..
        if (isReachedLast || isLoading) { return }

        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getPostsData(nexPageKey)
                Log.d(TAG, "response => $response")
                if (response.success) {
                    val newPosts = posts.value.toMutableList()
                    if (isInitialCall) {
                        _posts.emit(response.data)
                    } else {
                        newPosts.addAll(response.data)
                        _posts.emit(newPosts)
                    }

                    postDatabase.withTransaction {
                        postDao.deletePosts() // delete prev
                        postDao.insertPosts(response.data.map { it.toPostEntity() }) // add new
                    }

                    nexPageKey = response.next
                    isReachedLast = nexPageKey.isEmpty()
                } else {
                    // failed to get data from server
                    Log.d(TAG, "Invalid response => $response")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to data from server => $e")
            }
            isLoading = false
        }
    }


    // Pagination3 ex
    /*@OptIn(ExperimentalPagingApi::class)
    fun getPosts(): Flow<PagingData<Post>> =
        Pager(
            config = PagingConfig(
                pageSize = 3,
                prefetchDistance = 0,
                initialLoadSize = 3
            ),
            pagingSourceFactory = {
                //postDao.getPosts()
            },
            remoteMediator = PostsRemoteMediator(
                apiService,
                postDatabase,
                postDao,
                keysDao
            )
        ).flow*/


}