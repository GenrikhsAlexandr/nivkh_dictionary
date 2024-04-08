package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.WordApplication
import com.aleksandrgenrikhs.nivkhdictionary.databinding.ActivityMainBinding
import com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs.ERROR_MESSAGE_KEY
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        (applicationContext as WordApplication).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        getWords()
    }

    private fun getWords() {
        viewModel.viewModelScope.launch {
            when (val word = viewModel.getWordsForStart()) {
                is ResultState.Success -> startFragment()
                is ResultState.Error ->
                    startErrorActivity(word.message)
            }
        }
    }

    private fun startFragment() {
        setContentView(binding.root)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<MainFragment>(R.id.container)
        }
    }

    private fun startErrorActivity(errorMessage: Int) {
        val intent = Intent(this, ErrorActivity::class.java)
        intent.putExtra(ERROR_MESSAGE_KEY, errorMessage)
        startActivity(intent)
    }
}