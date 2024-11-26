package com.geeks.cleanArch.presentation.fragments.detail

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.domain.result.Result
import com.geeks.cleanArch.R
import com.geeks.cleanArch.databinding.FragmentDetailBinding
import com.geeks.cleanArch.presentation.fragments.TaskViewModel
import com.geeks.cleanArch.presentation.model.TaskUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding(FragmentDetailBinding::bind)
    private val viewModel: TaskViewModel by viewModel()
    private val navArgs by navArgs<DetailFragmentArgs>()

    private var taskUI: TaskUI? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTask(navArgs.taskId)
        setupListeners()
        updateUI()


        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.tasksFlow.collect { task ->
                task.let {
                    taskUI = task[id]
                    updateUI()
                }
            }
        }

        viewModel.viewModelScope.launch(Dispatchers.Main) {
            viewModel.taskStateFlow.collectLatest {
                when (it) {
                    is Result.Success -> {
                        taskUI = it.data
                    }

                    is Result.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is Result.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }
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