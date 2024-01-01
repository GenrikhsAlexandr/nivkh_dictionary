package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            println("isNetworkConnected = ${isNetworkConnected()}")
            if (isNetworkConnected()) {
                startMainFragment()
            } else startErrorActivity()
            binding.lottieAnimationView.isVisible = false
        }, 3000)
    }

    private fun startErrorActivity() {
        val intent = Intent(this@MainActivity, ErrorActivity::class.java)
        startActivity(intent)
    }

    private fun startMainFragment() {
        supportFragmentManager.commit {
            replace(R.id.container, MainFragment.newInstance())
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}