package com.geeks.cleanArch.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.geeks.cleanArch.databinding.ActivitySecondBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SecondActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySecondBinding.inflate(layoutInflater)
    }
    private val viewModel: MainActivityViewModel by viewModel()
    private val taskAdapter = TaskAdapter(emptyList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initAdapter()
        initObservers()
        viewModel.loadTasks()
    }

    private fun initAdapter() {
        binding.rvTask.adapter = taskAdapter
    }

    private fun initObservers() {
        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
        }
    }
}