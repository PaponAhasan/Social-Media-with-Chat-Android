package com.example.socialmedia.models

data class Message (
    val text: String? = null,
    val messageBy: User = User(),
    val messageTo: User = User(),
    val imageUrl: String? = null,
    val createdAt: Long = 0L,
    val likeBy: ArrayList<String> = ArrayList()
)