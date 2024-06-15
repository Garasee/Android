package com.example.garasee.view.main

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.garasee.R
import com.example.garasee.databinding.ActivityMainBinding
import com.example.garasee.di.Injection
import com.example.garasee.helper.ViewModelFactory
import com.example.garasee.view.history.HistoryFragment
import com.example.garasee.view.home.HomeFragment
import com.example.garasee.view.profile.ProfileFragment
import com.example.garasee.view.welcome.WelcomeActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: MainViewModel
    private lateinit var token: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        intent?.let { intent ->
            if (intent.hasExtra("fragment")) {
                val fragmentTag = intent.getStringExtra("fragment")
                if (fragmentTag == "profile") {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    navigationView.setCheckedItem(R.id.nav_profile)
                    return@onCreate
                }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        lifecycleScope.launch {

            val userRepository = Injection.provideUserRepository(applicationContext)
            val userPreference = Injection.provideUserPreference(applicationContext)
            token = userPreference.getToken().firstOrNull() ?: ""
            Log.d("MainActivity", "Token: $token")

            if (token.isEmpty()) {
                navigateToWelcome()
                return@launch
            }

            val isLoggedIn = userPreference.isLoggedin().firstOrNull() ?: ""
            Log.d("MainActivity", "IsLoggedIn: $isLoggedIn")

            token = userPreference.getToken().firstOrNull() ?: ""
            Log.d("MainActivity", "Token: $token")

            if (token.isEmpty()) {
                navigateToWelcome()
                return@launch
            }

            val isValidToken = userRepository.validateToken()
            Log.d("MainActivity", "IsValidToken: $isValidToken")

            if (!isValidToken) {
                navigateToWelcome()
                return@launch
            }
            if (isLoggedIn as Boolean) {
                try {
                    val factory = ViewModelFactory.getInstance(application as Application)
                    viewModel = ViewModelProvider(this@MainActivity, factory)[MainViewModel::class.java]

                } catch (e: IllegalStateException) {
                    Log.e("MainActivity", "IllegalStateException caught", e)
                    navigateToWelcome()
                }
            } else {
                navigateToWelcome()
            }
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_history -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryFragment()).commit()
            R.id.nav_profile -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()

            R.id.nav_logout -> lifecycleScope.launch {
                viewModel.logout()
                navigateToWelcome()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

