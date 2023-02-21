package com.example.socialmedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.adapter.UserAdapter
import com.example.socialmedia.daos.UserDao
import com.example.socialmedia.models.Post
import com.example.socialmedia.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ListChatFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var userDao: UserDao
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDao = UserDao()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val postsCollections = userDao.userCollection
        //val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<User>().setQuery(postsCollections, User::class.java).build()

        userAdapter = UserAdapter(recyclerViewOptions)

        val userRvId = activity?.findViewById<RecyclerView>(R.id.userListRv)

        userRvId!!.adapter = userAdapter
        userRvId.layoutManager = LinearLayoutManager(context)
        userRvId.itemAnimator = null
    }

    override fun onStart() {
        super.onStart()
        userAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        userAdapter.stopListening()
    }
}