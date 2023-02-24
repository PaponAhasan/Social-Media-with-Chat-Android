package com.example.socialmedia.daos

import com.example.socialmedia.models.Post
import com.example.socialmedia.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class PostDao {

    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("posts")

    fun addPost(text: String) {
        val currentUser = auth.currentUser!!.uid

        CoroutineScope(Dispatchers.IO).launch {
            //withContext(Dispatchers.IO) {
                val userDao = UserDao()
                val user = userDao.getUserById(currentUser).await().toObject(User::class.java)!!
                val currentTime = System.currentTimeMillis()
                val post = Post(text, user, currentTime)
                postCollections.document().set(post)
            //}
        }
    }

    private fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }

    fun updateLikes(postId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            //withContext(Dispatchers.IO) {
                val currentUserId = auth.currentUser!!.uid
                val post = getPostById(postId).await().toObject(Post::class.java)!!
                val isLiked = post.likeBy.contains(currentUserId)

                if (isLiked) {
                    post.likeBy.remove(currentUserId)
                } else {
                    post.likeBy.add(currentUserId)
                }
                postCollections.document(postId).set(post)
            //}
        }
    }

    fun deletePost(postId: String) {
        postCollections.document(postId).delete()
    }

    fun editPost(postId: String, text: String) {
        val currentUser = auth.currentUser!!.uid

        CoroutineScope(Dispatchers.IO).launch {
            //withContext(Dispatchers.IO) {
                val userDao = UserDao()
                val user = userDao.getUserById(currentUser).await().toObject(User::class.java)!!
                val currentTime = System.currentTimeMillis()
                val post = Post(text, user, currentTime)
                postCollections.document(postId).set(post)
            //}
        }
    }
}