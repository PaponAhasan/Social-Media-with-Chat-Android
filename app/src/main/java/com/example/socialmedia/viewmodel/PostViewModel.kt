package com.example.socialmedia.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialmedia.models.Post
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostViewModel : ViewModel() {
    private lateinit var db: FirebaseFirestore
    private var postLiveData: MutableLiveData<MutableList<Post>> = MutableLiveData()

    companion object {
        private const val TAG = "PostViewModel"
    }

    init {
        postLiveData.value = getFirebasePostData()
    }

    fun getPosts(): LiveData<MutableList<Post>> {
        return postLiveData
    }

    private fun getFirebasePostData(): MutableList<Post> {
        val userPostList = arrayListOf<Post>()
        db = FirebaseFirestore.getInstance()
        val postCollections = db.collection("posts")
        postCollections
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (dc: DocumentChange in snapshot.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            userPostList.add(dc.document.toObject(Post::class.java))
                        }
                    }

                }

            }

        return userPostList
    }
}