package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.WordApplication
import com.aleksandrgenrikhs.nivkhdictionary.databinding.ActivityMainBinding
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var reposutory: WordRepository
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        (applicationContext as WordApplication).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            error()
            binding.lottieAnimationView.isVisible = false
        }, 3000)
    }

    private fun error() {
        lifecycleScope.launch {
            if (!reposutory.error()) {
                startMainFragment()
            } else {
                startErrorActivity()
            }
        }
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
}