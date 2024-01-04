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
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.WordApplication
import com.aleksandrgenrikhs.nivkhdictionary.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        (applicationContext as WordApplication).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            if (viewModel.countWWord.value == 0) {
                if (isNetworkConnected()) {
                    viewModel.viewModelScope.launch {
                        viewModel.getAndSaveWords()
                        startMainFragment()
                    }
                } else {
                    startErrorActivity()
                }
            } else {
                startMainFragment()
            }
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