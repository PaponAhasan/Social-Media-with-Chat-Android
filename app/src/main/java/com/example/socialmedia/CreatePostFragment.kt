package com.example.socialmedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postDao = PostDao()

        binding.postBtn.setOnClickListener {
            val postText = binding.postEt.text.toString().trim()
            if (postText.isNotEmpty()) {
                postDao.addPost(postText)

                val action = CreatePostFragmentDirections.actionCreatePostFragmentToHomeFragment()
                findNavController().navigate(action)
            }
            else{
                Toast.makeText(context, "text required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}