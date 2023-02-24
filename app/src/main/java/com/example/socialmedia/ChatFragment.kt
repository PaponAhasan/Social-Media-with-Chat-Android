package com.example.socialmedia

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.adapter.IMessageAdapter
import com.example.socialmedia.adapter.MessageAdapter
import com.example.socialmedia.daos.MessageDao
import com.example.socialmedia.databinding.FragmentChatBinding
import com.example.socialmedia.models.Message
import com.example.socialmedia.utils.ButtonObserver
import com.example.socialmedia.utils.OpenDocumentContract
import com.example.socialmedia.utils.ScrollToBottomObserver
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatFragment : Fragment(), IMessageAdapter {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val args: ChatFragmentArgs by navArgs()

    private lateinit var messageToId: String
    private lateinit var messageToName: String
    private lateinit var manager: LinearLayoutManager

    private lateinit var auth: FirebaseAuth
    private lateinit var messageDao: MessageDao
    private lateinit var messageAdapter: MessageAdapter

    private val imageRef = Firebase.storage.reference

    companion object {
        private const val TAG = "ChatFragment"
        private const val MESSAGES_CHILD = "messages"
    }

    //For image
    private val openDocument = registerForActivityResult(OpenDocumentContract()) { uri ->
        uri?.let { putImageInStorage(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageToId = args.recipientId
        messageToName = args.recipientName

        (activity as AppCompatActivity).supportActionBar!!.title = messageToName

        messageDao = MessageDao()
        auth = Firebase.auth

        //binding.progressBar.visibility = View.INVISIBLE

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }

        storeMessage()

        setUpRecyclerView()
    }

    private fun storeMessage() {
        val messageSendId = binding.sendMessageBtn
        val messageTextId = binding.inputMessage
        messageTextId.addTextChangedListener(ButtonObserver(messageSendId))
        messageSendId.setOnClickListener {
            val messageText = messageTextId.text.toString().trim()
            if (messageText.isNotEmpty()) {
                messageDao.sendMessage(messageText, messageToId, null)
                messageTextId.setText("")
            } else Log.e(TAG, "Message Is Empty")
        }
    }

    private fun setUpRecyclerView() {

        val postsCollections = messageDao.msgCollections

        val query = postsCollections.orderBy("createdAt", Query.Direction.ASCENDING)

        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()

        messageAdapter = MessageAdapter(recyclerViewOptions, messageToId, this)
        manager = LinearLayoutManager(context)
        manager.stackFromEnd = true
        binding.userMessageRv.layoutManager = manager
        binding.userMessageRv.adapter = messageAdapter
        binding.userMessageRv.itemAnimator = null

        // Scroll down when a new message arrives
        messageAdapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.userMessageRv, messageAdapter, manager)
        )
    }

    private fun putImageInStorage(uri: Uri) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val fileName = UUID.randomUUID().toString()
            imageRef.child("$MESSAGES_CHILD/$fileName").putFile(uri).await()
            withContext(Dispatchers.Main) {
                messageDao.sendMessage(null, messageToId, uri.toString())
                Toast.makeText(context, "Successfully uploaded image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        onSplashFinished()
        messageAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        messageAdapter.stopListening()
    }

    override fun onLikeClicked(messageId: String) {
        messageDao.updateLikes(messageId)
    }

    override fun onMessageDeleteClicked(messageId: String) {
        messageDao.deleteMessage(messageId)
    }

    private fun onSplashFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("splashFinished", true)
        editor.apply()
    }
}