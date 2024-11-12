package com.geeks.cleanArch.presentation.fragments.addTask

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.geeks.cleanArch.R
import com.geeks.cleanArch.databinding.FragmentAddTaskBinding
import com.geeks.cleanArch.presentation.model.TaskUI
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddTaskFragment : Fragment(R.layout.fragment_add_task) {

    private val binding by viewBinding(FragmentAddTaskBinding::bind)
    private val viewModel: TaskViewModel by viewModel()
    private var imageString: String? = null
    private val imageLauncher: ActivityResultLauncher<String> by lazy {
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.changeImage.setImageURI(uri)
                imageString = uri.toString()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()

        lifecycleScope.launchWhenCreated {
            viewModel.loadingFlow.collect { state ->
                when(state) {
                    is LoadingState.Loading -> {}
                    is LoadingState.Error -> {
                        Toast.makeText(requireContext(), "Error inserting task", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupListeners() {

        binding.changeImage.setOnClickListener {
            imageLauncher.launch("image/*")
        }

        binding.btnAddTask.setOnClickListener {
            val task = binding.tvTask.text.toString()
            val date = binding.tvDate.text.toString()

            val taskUI = TaskUI(0, task, date, imageString.toString())
            viewModel.insertTask(taskUI)

            viewModel.viewModelScope.launch {
                viewModel.insertMessageFlow.collectLatest {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
            findNavController().navigate(R.id.action_AddTaskFragment_to_taskListFragment)
        }
    }
}