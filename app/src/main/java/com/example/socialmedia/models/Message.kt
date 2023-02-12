package com.example.socialmedia.models

data class Message (
    val text: String = "",
    val messageBy: User = User(),
    val messageTo: User = User(),
    val createdAt: Long = 0L,
    val likeBy: ArrayList<String> = ArrayList()
)