package com.example.socialmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.models.Post
import com.example.socialmedia.models.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserAdapter(options: FirestoreRecyclerOptions<User>) :
    FirestoreRecyclerAdapter<User, UserAdapter.UserViewHolder>(options) {

    private val currentUser = Firebase.auth.currentUser

    companion object{
        private const val TAG = "UserAdapter"
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userName: TextView = itemView.findViewById(R.id.userNameTV)
        val userMsg: TextView = itemView.findViewById(R.id.userMsgTV)
        val userImage: ImageView = itemView.findViewById(R.id.userIv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
        holder.userName.text = model.displayName
        Glide.with(holder.itemView.context).load(model.imageUrl).override(500, 200).circleCrop()
            .into(holder.userImage)

    }
}