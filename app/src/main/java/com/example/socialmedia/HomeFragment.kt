package com.example.socialmedia

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.adapter.IPostAdapter
import com.example.socialmedia.adapter.PostAdapter
import com.example.socialmedia.daos.PostDao
import com.example.socialmedia.databinding.FragmentHomeBinding
import com.example.socialmedia.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(), IPostAdapter {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter
    private lateinit var postDao: PostDao
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.postBtn.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToCreatePostFragment()
            findNavController().navigate(action)
        }

        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = false
        }

        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        postDao = PostDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        postAdapter = PostAdapter(recyclerViewOptions, this)

        binding.postsRv.adapter = postAdapter
        binding.postsRv.layoutManager = LinearLayoutManager(context)
        binding.postsRv.itemAnimator = null
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)

        postAdapter.startListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onPostDeleteClicked(postId: String) {
        postDao.deletePost(postId)
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser == null) {
            Navigation.findNavController(activity as Activity, R.id.nav_host_fragment).navigate(R.id.signInFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        postAdapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}