package com.geeks.cleanArch.presentation.fragments.taskList

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.geeks.cleanArch.R
import com.geeks.cleanArch.databinding.FragmentTaskListBinding
import com.geeks.cleanArch.presentation.fragments.adapter.TaskAdapter
import com.geeks.cleanArch.presentation.fragments.addTask.LoadingState
import com.geeks.cleanArch.presentation.fragments.addTask.TaskViewModel
import com.geeks.cleanArch.presentation.model.TaskUI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private val binding by viewBinding(FragmentTaskListBinding::bind)
    private val viewModel: TaskViewModel by viewModel()
    private lateinit var taskAdapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadTasks()
        addTask()
        initialize()
        showTask()

        lifecycleScope.launchWhenCreated {
            viewModel.loadingFlow.collect { state ->
                when (state) {
                    is LoadingState.Loading -> {}
                    is LoadingState.Error -> {
                        Toast.makeText(requireContext(), "Error loading task", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun addTask() {
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_AddTaskFragment)
        }
    }

    private fun initialize() {
        taskAdapter = TaskAdapter(emptyList(), { task ->
            viewModel.viewModelScope.launch {
                viewModel.getTask(id)
            }
            val action = TaskListFragmentDirections.actionTaskListFragmentToDetailFragment(task)
            findNavController().navigate(action)
        }, { task ->
            viewModel.deleteTask(task)
        })
        binding.rvTask.adapter = taskAdapter
        taskAdapter.attachSwipeToRecyclerView(binding.rvTask)
    }

    private fun showTask() {
        viewModel.viewModelScope.launch {
            viewModel.tasksFlow.collectLatest {
                taskAdapter.updateTasks(it)
            }
        }
    }

}