package com.example.socialmedia.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.socialmedia.R
import com.example.socialmedia.onboarding.screens.FirstScreen
import com.example.socialmedia.onboarding.screens.SecondScreen
import com.example.socialmedia.onboarding.screens.ThirdScreen

class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_pager, container, false)

        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.GONE

        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view?.findViewById<ViewPager2>(R.id.viewPager)?.adapter = adapter

        return view
    }
}