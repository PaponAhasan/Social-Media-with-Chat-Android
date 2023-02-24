package com.example.socialmedia

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.socialmedia.daos.PostDao
import com.example.socialmedia.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private lateinit var postDao: PostDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postDao = PostDao()

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        val menuItemA = menu.findItem(R.id.SaveId)
        menuItemA.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.SaveId -> {
                    // Handle the click event for action item A
                    val postText = binding.postEt.text.toString().trim()
                    if (postText.isNotEmpty()) {
                        postDao.addPost(postText)

                        Navigation.findNavController(context as Activity, R.id.nav_host_fragment).navigate(R.id.homeFragment)

//                        val action = CreatePostFragmentDirections.actionCreatePostFragmentToHomeFragment()
//                        findNavController().navigate(action)
                    }
                    else{
                        Toast.makeText(context, "text required", Toast.LENGTH_SHORT).show()
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.setGroupVisible(R.id.groupA, false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}