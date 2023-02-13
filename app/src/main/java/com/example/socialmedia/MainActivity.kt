package com.example.socialmedia

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.socialmedia.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        drawerLayout = findViewById(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setSupportActionBar(findViewById(R.id.toolbar))
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.chatFragment -> {
                    val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
                    navController.navigate(R.id.listChatFragment)
                    drawerLayout.close()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_history -> {
                    Toast.makeText(this, "Communing Soon..", Toast.LENGTH_SHORT)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_logout -> {
                    Firebase.auth.signOut()
                    startActivity(
                        Intent(this, SignInActivity::class.java)
                    )
                    finish()
                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener true
            }
        }

        val headerView = binding.navView.getHeaderView(0)
        val headerViewName = headerView.findViewById<TextView>(R.id.headerName)
        val headerViewImage = headerView.findViewById<ImageView>(R.id.headerImage)
        if (auth.currentUser != null) {
            headerViewName.text = auth.currentUser!!.displayName
            Glide.with(this)
                .load(auth.currentUser!!.photoUrl)
                .circleCrop()
                .override(500, 200)
                .into(headerViewImage)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

}