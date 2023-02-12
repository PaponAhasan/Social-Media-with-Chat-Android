package com.example.socialmedia

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
        postDao = PostDao()

        if (auth.currentUser != null && auth.currentUser!!.displayName != null) {
            (activity as AppCompatActivity).supportActionBar!!.title =
                auth.currentUser!!.displayName

        }
        binding.postBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCreatePostFragment()
            findNavController().navigate(action)
        }
        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = false
        }

        setUpRecyclerView()

        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            backPressedCallback
        )
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (childFragmentManager.backStackEntryCount > 0) {
                // If there are fragments in the back stack, pop the back stack
                childFragmentManager.popBackStack()
            } else {
                // If there are no fragments in the back stack, close the app
                requireActivity().finishAffinity()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        val menuItemAMessage = menu.findItem(R.id.messageId)
        menuItemAMessage.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.messageId -> {
                    // Handle the click event for action item A
                    val action = HomeFragmentDirections.actionHomeFragmentToListChatFragment()
                    findNavController().navigate(action)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }

        val menuItemAProfile = menu.findItem(R.id.menu_profile)
        menuItemAProfile.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_profile -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
                    findNavController().navigate(action)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }

        val menuItemALogout = menu.findItem(R.id.menu_logout)
        menuItemALogout.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_logout -> {
                    Firebase.auth.signOut()
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.setGroupVisible(R.id.groupB, false)
        super.onPrepareOptionsMenu(menu)
    }

    private fun setUpRecyclerView() {
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        postAdapter = PostAdapter(recyclerViewOptions, this)

        binding.postsRv.adapter = postAdapter
        binding.postsRv.layoutManager = LinearLayoutManager(context)
        binding.postsRv.itemAnimator = null
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onPostDeleteClicked(postId: String) {
        postDao.deletePost(postId)
    }

    override fun onEditClickedListener(postId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToEditPostFragment(postId)
        findNavController().navigate(action)
    }

    override fun onUserMessageListener(postId: String) {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)

        postAdapter.startListening()
    }


    override fun onStop() {
        super.onStop()
        postAdapter.stopListening()
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            val intent = Intent(activity, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}