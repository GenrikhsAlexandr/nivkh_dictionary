package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aleksandrgenrikhs.nivkhdictionary.databinding.ActivityErrorBinding

class ErrorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        binding.refreshButton.setOnClickListener {
            val intent = Intent(this@ErrorActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContentView(binding.root)
    }
}