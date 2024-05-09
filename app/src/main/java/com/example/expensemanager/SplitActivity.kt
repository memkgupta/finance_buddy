package com.example.expensemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanager.databinding.ActivitySplitBinding

class SplitActivity : AppCompatActivity() {
    lateinit var binding:ActivitySplitBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplitBinding.inflate(layoutInflater);

        setContentView(binding.root);

    }
}