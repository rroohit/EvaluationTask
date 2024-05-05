package com.roh.evaluationtask.util

import com.roh.evaluationtask.data.local.PostEntity
import com.roh.evaluationtask.domain.model.Post

fun PostEntity.toPost() = Post(
    id = id,
    title = title,
    thumbnail = thumbnail,
    name = name,
    username = username,
    profilePic = profilePic,
    dated = dated,
    type = type
)


fun Post.toPostEntity() = PostEntity(
    id = id,
    title = title,
    thumbnail = thumbnail,
    name = name,
    username = username,
    profilePic = profilePic,
    dated = dated,
    type = type
)
