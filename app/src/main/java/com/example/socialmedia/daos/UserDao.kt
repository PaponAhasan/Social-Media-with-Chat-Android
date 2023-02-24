package com.example.socialmedia.daos

import com.example.socialmedia.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("users")

    fun allUser(user: User?) {
        user?.let {
            CoroutineScope(Dispatchers.IO).launch {
                userCollection.document(user.uid).set(it)
            }
        }
    }

    fun getUserById(uID: String): Task<DocumentSnapshot> {
        return userCollection.document(uID).get()
    }
}