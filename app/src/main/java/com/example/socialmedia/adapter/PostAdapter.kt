package com.example.socialmedia.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.models.Post
import com.example.socialmedia.utils.Utils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>, private val listener: IPostAdapter) :
    FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
        options
    ) {

    private val currentUser = Firebase.auth.currentUser

    companion object {
        private const val TAG = "PostAdapter"
    }
    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val textOption: TextView = itemView.findViewById(R.id.textOption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
        viewHolder.likeButton.setOnClickListener {
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.absoluteAdapterPosition).id)
        }
        viewHolder.userText.setOnClickListener {
            if(viewHolder.userText.text != currentUser!!.displayName){
                listener.onUserMessageListener(snapshots.getSnapshot(viewHolder.absoluteAdapterPosition).id)
           }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop()
            .into(holder.userImage)
        holder.likeCount.text = model.likeBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)

        val isLiked = model.likeBy.contains(currentUser?.uid)
        Log.e(TAG, "post $isLiked")
        if (isLiked) {
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context, R.drawable.ic_liked
                )
            )
        } else {
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context, R.drawable.ic_unlike
                )
            )
        }

        if (model.createdBy.uid != currentUser?.uid){
            holder.textOption.visibility = View.INVISIBLE
        }

        holder.textOption.setOnClickListener {
            postEditDeletePopMenu(holder, position, model)
        }
    }

    private fun postEditDeletePopMenu(holder: PostViewHolder, position: Int, model: Post) {
        val popupMenu = PopupMenu(holder.itemView.context, holder.textOption)
        popupMenu.menuInflater.inflate(R.menu.recyclerviw_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.post_edit ->{
                    listener.onEditClickedListener(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
                }

                R.id.post_delete ->
                    if (model.createdBy.uid == currentUser!!.uid) {

                        val builder = AlertDialog.Builder(holder.itemView.context)
                        with(builder) {
                            setTitle("Delete")
                            setMessage("Are you sure?")
                            setIcon(
                                ContextCompat.getDrawable(
                                    holder.itemView.context,
                                    android.R.drawable.ic_dialog_alert
                                )
                            )
                        }
                        builder.setPositiveButton("Yes") { dialog, which ->
                            // Perform some action when the positive button is clicked
                            listener.onPostDeleteClicked(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
                        }
                        builder.setNegativeButton("No") { dialog, which ->
                            // Perform some action when the negative button is clicked
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.show()
                    } else {
                        Log.d(TAG, "You are not the owner of this post!")
                    }
            }
            true
        }
        popupMenu.show()
    }
}
interface IPostAdapter {
    fun onLikeClicked(postId: String)
    fun onPostDeleteClicked(postId: String)
    fun onEditClickedListener(postId: String)
    fun onUserMessageListener(postId: String)
}