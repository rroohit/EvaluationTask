package com.roh.evaluationtask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.roh.evaluationtask.domain.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("SELECT * FROM Posts")
    fun getPosts(): Flow<List<PostEntity>>

    @Query("DELETE FROM posts")
    suspend fun deletePosts()
}