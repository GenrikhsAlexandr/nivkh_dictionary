package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.WordApplication
import com.aleksandrgenrikhs.nivkhdictionary.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { !viewModel.isReady.value }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        (applicationContext as WordApplication).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getWordsForStart()
        subscribe()
        clickRefresh()
    }

    private fun subscribe() {
        with(binding) {
        viewModel.viewModelScope.launch {
                viewModel.isErrorLayoutVisible.collect { isVisible ->
                    errorLayout.isVisible = !isVisible
                    if (isVisible) {
                        startFragment()
                    }
                }
            }
            viewModel.viewModelScope.launch {
                viewModel.toastMessage.collect { message ->
                    errorTitle.setText(message)
                }
            }
            viewModel.viewModelScope.launch {
                viewModel.isProgressBarVisible.collect { isVisible ->
                    progressBar.isVisible = isVisible
                    errorTitle.isVisible = !isVisible
                }
            }
        }
    }

    private fun clickRefresh() {
        binding.refreshButton.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun startFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<MainFragment>(R.id.container)
        }
    }
}