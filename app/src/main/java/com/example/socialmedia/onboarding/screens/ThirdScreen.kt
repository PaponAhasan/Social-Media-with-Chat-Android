package com.example.socialmedia.onboarding.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.socialmedia.R

class ThirdScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_third_screen, container, false)

        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.GONE

        view?.findViewById<TextView>(R.id.finish)?.setOnClickListener {
            findNavController().navigate(
                R.id.action_viewPagerFragment_to_signInActivity,
                null, NavOptions.Builder()
                    .setPopUpTo(
                        R.id.splashFragment,
                        true
                    ).build()
            )
            onBoardingFinished()
        }

        return view
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}