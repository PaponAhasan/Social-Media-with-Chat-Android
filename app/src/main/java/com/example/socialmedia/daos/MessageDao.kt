package com.example.socialmedia.daos

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MessageDao {

    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("messages")
}