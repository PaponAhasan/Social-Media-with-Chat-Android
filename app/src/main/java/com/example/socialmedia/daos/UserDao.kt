package com.example.socialmedia.daos

import com.example.socialmedia.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun allUser(user: User?){
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }
        }
    }

    fun getUserById(uID: String): Task<DocumentSnapshot> {
        return userCollection.document(uID).get()
    }
}