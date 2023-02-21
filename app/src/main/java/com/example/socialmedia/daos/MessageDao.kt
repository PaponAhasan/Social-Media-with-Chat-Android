package com.example.socialmedia.daos

import com.example.socialmedia.models.Message
import com.example.socialmedia.models.Post
import com.example.socialmedia.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MessageDao {

    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    val msgCollections = db.collection("messages")

    fun sendMessage(text: String, messageToId: String) {
        val currentUser = auth.currentUser!!.uid

        GlobalScope.launch {
            withContext(Dispatchers.IO){
                val userDao = UserDao()
                val messageBy = userDao.getUserById(currentUser).await().toObject(User::class.java)!!
                val messageTo = userDao.getUserById(messageToId).await().toObject(User::class.java)!!
                val currentTime = System.currentTimeMillis()
                val message = Message(text, messageBy, messageTo, currentTime)
                msgCollections.document().set(message)
            }
        }
    }

    private fun getMessageById(postId: String): Task<DocumentSnapshot> {
        return msgCollections.document(postId).get()
    }

    fun updateLikes(postId: String) {
        GlobalScope.launch {
            withContext(Dispatchers.IO){
                val currentUserId = auth.currentUser!!.uid
                val message = getMessageById(postId).await().toObject(Message::class.java)!!
                val isLiked = message.likeBy.contains(currentUserId)

                if (isLiked) {
                    message.likeBy.remove(currentUserId)
                } else {
                    message.likeBy.add(currentUserId)
                }
                msgCollections.document(postId).set(message)
            }
        }
    }
}