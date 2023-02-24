package com.example.socialmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.models.Message
import com.example.socialmedia.utils.Utils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(options: FirestoreRecyclerOptions<Message>, private val messageToId: String,
                     private val listener: IMessageAdapter) :
    FirestoreRecyclerAdapter<Message, MessageAdapter.MessageViewHolder>(options) {

    private val currentUser = Firebase.auth.currentUser

    companion object {
        private const val TAG = "MessageAdapter"
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val receiverLayout: LinearLayout = itemView.findViewById(R.id.receiverMessageLayout)
        val receiverName: TextView = itemView.findViewById(R.id.receiver_message_name)
        val receiverMessage: TextView = itemView.findViewById(R.id.receiver_message)
        val receiverDate: TextView = itemView.findViewById(R.id.receiver_message_date)
        val receiverProfile: CircleImageView = itemView.findViewById(R.id.message_profile_image)
        val receiverLikeCount: TextView = itemView.findViewById(R.id.receiverMsgLikeCount)
        val receiverLikeButton: ImageView = itemView.findViewById(R.id.receiverMsgLikeButton)

        val senderLayout: LinearLayout = itemView.findViewById(R.id.senderMessageLayout)
        val senderName: TextView = itemView.findViewById(R.id.sender_message_name)
        val senderMessage: TextView = itemView.findViewById(R.id.sender_message)
        val senderDate: TextView = itemView.findViewById(R.id.sender_message_date)
        val senderMessageImage: ImageView = itemView.findViewById(R.id.sender_message_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val viewHolder =  MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.message_layout, parent, false)
        )

        viewHolder.receiverLikeButton.setOnClickListener {
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.absoluteAdapterPosition).id)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        val messageSenderId = model.messageBy.uid
        val messageReceiverId = model.messageTo.uid

        holder.receiverName.visibility = View.GONE
        holder.receiverMessage.visibility = View.GONE
        holder.receiverDate.visibility = View.GONE
        holder.receiverProfile.visibility = View.GONE
        holder.receiverLikeButton.visibility = View.GONE
        holder.receiverLikeCount.visibility = View.GONE

        holder.senderName.visibility = View.GONE
        holder.senderMessage.visibility = View.GONE
        holder.senderDate.visibility = View.GONE
        holder.senderMessageImage.visibility = View.GONE

        if (messageSenderId == messageToId || messageReceiverId == messageToId) {

            if (model.messageBy.uid == currentUser!!.uid) {

                holder.senderName.visibility = View.VISIBLE
                holder.senderDate.visibility = View.VISIBLE

                holder.senderName.text = model.messageBy.displayName
                if(model.text != null){
                    holder.senderMessage.visibility = View.VISIBLE
                    holder.senderMessage.text = model.text
                }
                else{
                    holder.senderMessageImage.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(model.imageUrl)
                        .into(holder.senderMessageImage)
                }
                holder.senderDate.text = Utils.getTimeAgo(model.createdAt)
                holder.itemView.setOnClickListener {
                    messageDeletePopMenu(holder)
                }

                holder.receiverName.visibility = View.GONE
                holder.receiverMessage.visibility = View.GONE
                holder.receiverDate.visibility = View.GONE
                holder.receiverProfile.visibility = View.GONE
                holder.receiverLikeButton.visibility = View.GONE
                holder.receiverLikeCount.visibility = View.GONE
            } else if (model.messageTo.uid == currentUser.uid) {

                holder.receiverName.visibility = View.VISIBLE
                holder.receiverMessage.visibility = View.VISIBLE
                holder.receiverDate.visibility = View.VISIBLE
                holder.receiverProfile.visibility = View.VISIBLE
                holder.receiverLikeButton.visibility = View.VISIBLE
                holder.receiverLikeCount.visibility = View.VISIBLE

                holder.receiverName.text = model.messageBy.displayName
                holder.receiverMessage.text = model.text
                holder.receiverDate.text = Utils.getTimeAgo(model.createdAt)
                Glide.with(holder.itemView.context).load(model.messageBy.imageUrl)
                    .circleCrop()
                    .into(holder.receiverProfile)

                holder.receiverLikeCount.text = model.likeBy.size.toString()

                val isLiked = model.likeBy.contains(currentUser.uid)
                if (isLiked) {
                    holder.receiverLikeButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.itemView.context, R.drawable.ic_liked
                        )
                    )
                } else {
                    holder.receiverLikeButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.itemView.context, R.drawable.ic_unlike
                        )
                    )
                }

                holder.senderName.visibility = View.GONE
                holder.senderMessage.visibility = View.GONE
                holder.senderDate.visibility = View.GONE
                holder.senderMessageImage.visibility = View.GONE
            }
            else {
                holder.itemView.visibility = View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
        }
        else {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    private fun messageDeletePopMenu(holder: MessageViewHolder) {
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
            listener.onMessageDeleteClicked(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Perform some action when the negative button is clicked
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}

interface IMessageAdapter {
    fun onLikeClicked(messageId: String)
    fun onMessageDeleteClicked(messageId: String)
}