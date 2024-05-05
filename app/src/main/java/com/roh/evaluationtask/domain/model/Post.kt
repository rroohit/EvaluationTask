package com.roh.evaluationtask.domain.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: String,
    val title: String,
    val thumbnail: String,
    val name: String,
    val username: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    val dated: Long,
    val type: String
)