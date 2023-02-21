package com.example.socialmedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmedia.adapter.IPostAdapter
import com.example.socialmedia.adapter.IPostProfileAdapter
import com.example.socialmedia.adapter.ProfileAdapter
import com.example.socialmedia.daos.PostDao
import com.example.socialmedia.databinding.FragmentProfileBinding
import com.example.socialmedia.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(), IPostProfileAdapter {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var postDao: PostDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        postDao = PostDao()

        val profileViewName = binding.profileName
        val profileViewImage = binding.profileImage
        if (auth.currentUser != null) {
            profileViewName.text = auth.currentUser!!.displayName
            Glide.with(this)
                .load(auth.currentUser!!.photoUrl)
                .circleCrop()
                .override(500, 200)
                .into(profileViewImage)
        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val postsCollections = postDao.postCollections
        val query = postsCollections
            .whereEqualTo("createdBy.uid", auth.currentUser!!.uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        profileAdapter = ProfileAdapter(recyclerViewOptions, this)

        binding.profileRv.adapter = profileAdapter
        binding.profileRv.layoutManager = LinearLayoutManager(context)
        binding.profileRv.itemAnimator = null
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onPostDeleteClicked(postId: String) {
        postDao.deletePost(postId)
    }

    override fun onEditClickedListener(postId: String) {
        val action = ProfileFragmentDirections.actionProfileFragmentToEditPostFragment(postId)
        findNavController().navigate(action)
    }

//    override fun onUserMessageListener(postId: String) {
//
//        postDao.postCollections.document(postId).get().addOnSuccessListener {
//            val post = it.toObject(Post::class.java)
//            if(post != null){
//                val messageToId = post.createdBy.uid
//                val action = ProfileFragmentDirections.actionProfileFragmentToChatFragment(messageToId)
//                findNavController().navigate(action)
//            }
//        }
//
//        //Navigation.findNavController(context as Activity, R.id.nav_host_fragment).navigate(R.id.chatFragment)
//    }

    override fun onStart() {
        super.onStart()
        profileAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        profileAdapter.stopListening()
    }

//    override fun onResume() {
//        super.onResume()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}