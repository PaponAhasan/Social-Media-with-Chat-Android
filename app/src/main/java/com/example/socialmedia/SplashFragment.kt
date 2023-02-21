package com.example.socialmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.GONE

        auth = Firebase.auth

        Handler(Looper.getMainLooper()).postDelayed({
            // Check if the user has Completed Onboard
            if (onBoardingFinished() && userIsActive()) {
                findNavController().navigate(
                    R.id.action_splashFragment_to_homeFragment,
                    null, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.splashFragment,
                            true
                        ).build()
                )
                onSplashFinished()
            } else {
                findNavController().navigate(
                    R.id.action_splashFragment_to_viewPagerFragment,
                    null, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.splashFragment,
                            true
                        ).build()
                )
            }
        }, 2500)
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    private fun onSplashFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("splashFinished", true)
        editor.apply()
    }

    private fun userIsActive(): Boolean{
        val currentUser = auth.currentUser
        return currentUser != null
    }
}