package com.roh.evaluationtask.domain.model

data class ApiResponse(
    val success: Boolean,
    val data: List<Post>,
    val next: String
)