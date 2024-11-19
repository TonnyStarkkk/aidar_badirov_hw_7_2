package com.geeks.cleanArch.presentation.fragments.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.geeks.cleanArch.R
import com.geeks.cleanArch.databinding.FragmentDetailBinding
import com.geeks.cleanArch.presentation.fragments.addTask.LoadingState
import com.geeks.cleanArch.presentation.fragments.addTask.TaskViewModel
import com.geeks.cleanArch.presentation.model.TaskUI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.Task
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding(FragmentDetailBinding::bind)
    private val viewModel: TaskViewModel by viewModel()
    private val navArgs by navArgs<DetailFragmentArgs>()

    private var taskUI: TaskUI? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        updateUI()

        viewModel.loadTask(navArgs.taskId)

        viewModel.viewModelScope.launch {
            viewModel.taskStateFlow.collect { task ->
                task?.let {
                    taskUI = task
                    updateUI()
                }
            }
        }

        viewModel.viewModelScope.launch {
            viewModel.loadingFlow.collect {state ->
                when(state ) {
                    is LoadingState.Loading -> {}
                    is LoadingState.Error -> {
                        Toast.makeText(requireContext(), "Error updating task", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSaveChange.setOnClickListener {
            val updatedTask = taskUI?.copy(
                taskName = binding.tvTask.text.toString(),
                taskDate = binding.tvDate.text.toString()
            )
            updatedTask?.let {
                viewModel.updateTask(it)
                findNavController().navigateUp()
            }
        }
    }

    private fun updateUI() {
        binding.tvTask.setText(taskUI?.taskName)
        binding.tvDate.setText(taskUI?.taskDate)
        taskUI?.taskImage?.let {
            binding.addPhoto.setImageURI(Uri.parse(it))
        }
    }
}