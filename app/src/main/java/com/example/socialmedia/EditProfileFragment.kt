package com.example.socialmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.socialmedia.daos.UserDao
import com.example.socialmedia.databinding.FragmentEditProfileBinding
import com.example.socialmedia.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    var selectedImageUri: Uri? = null
    private lateinit var selectedImageBitmap: Bitmap

    private lateinit var auth: FirebaseAuth
    private val imageRef = Firebase.storage.reference

    companion object {
        private const val TAG = "ChatFragment"
        private const val MESSAGES_CHILD = "profile"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.userImageIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }

        binding.userSaveBtn.setOnClickListener {
            selectedImageUri?.let {
                putImageInStorage(it)
            }
        }
    }

    private fun putImageInStorage(uri: Uri?) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val fileName = UUID.randomUUID().toString()
            imageRef.child("${MESSAGES_CHILD}/$fileName").putFile(uri!!).await()
            withContext(Dispatchers.Main) {
                val userName = binding.userNameET.text.toString()
                if(userName.isNotEmpty()){
                    sendToMain(userName)
                }
                Toast.makeText(context, "Successfully uploaded image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendToMain(userName: String) {
        val currentUser = auth.currentUser

        val user =
            currentUser?.let {
                User(it.uid, userName, selectedImageUri.toString())
            }
        val userDao = UserDao()
        userDao.allUser(user)

        //startActivity(Intent(context, MainActivity::class.java))
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val uri: Intent? = result.data
                selectedImageUri = uri?.data

                if (selectedImageUri != null) {
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                            activity?.contentResolver,
                            selectedImageUri
                        )
                        binding.userImageIV.setImageBitmap(
                            selectedImageBitmap
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    override fun onStart() {
        onSplashFinished()
        super.onStart()
    }

    private fun onSplashFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("splashFinished", true)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}