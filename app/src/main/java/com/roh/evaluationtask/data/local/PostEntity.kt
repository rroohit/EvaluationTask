package com.roh.evaluationtask.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Posts"
)
data class PostEntity (
    @PrimaryKey
    val id: String,
    val title: String,
    val thumbnail: String,
    val name: String,
    val username: String,
    val profilePic: String,
    val dated: Long,
    val type: String
)