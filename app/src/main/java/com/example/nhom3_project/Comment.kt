package com.example.nhom3_project

data class Comment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val productId: String = "",
    val rating: Float = 0f,
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val productName: String = "",
    val productImage: String = ""
)