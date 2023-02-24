package com.example.socialmedia

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.socialmedia.daos.PostDao
import com.example.socialmedia.models.Post
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditPostFragment : Fragment() {

    private val args: EditPostFragmentArgs by navArgs()

    private lateinit var postDao: PostDao
    private lateinit var postId: String

    companion object{
        private const val TAG = "EditPostFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postDao = PostDao()

        postId = args.postId

        postDao.postCollections.document(postId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Document found in the offline cache
                val postText = task.result.getString("text")
                view.findViewById<EditText>(R.id.postEditEt).setText(postText)

            } else {
                Log.d(TAG, "Cached get failed: ", task.exception)
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        val menuItemA = menu.findItem(R.id.SaveId)
        menuItemA.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.SaveId -> {
                    // Handle the click event for action item A
                    val postText = requireView().findViewById<EditText>(R.id.postEditEt).text.toString().trim()
                    if (postText.isNotEmpty()) {
                        postDao.editPost(postId, postText)

                        //Navigation.findNavController(context as Activity, R.id.nav_host_fragment).navigate(R.id.profileFragment)

                        Navigation.findNavController(context as Activity, R.id.nav_host_fragment).navigate(R.id.homeFragment)


                        //Navigation.findNavController(context as Activity, R.id.nav_host_fragment).navigate(R.id.profileFragment)
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
}