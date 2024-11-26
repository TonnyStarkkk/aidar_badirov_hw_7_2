package com.geeks.cleanArch.presentation.fragments.taskList

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.ContentInfoCompat.Flags
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.addtaskfeature.addTask.activity.AddTaskActivity
import com.geeks.cleanArch.R
import com.geeks.cleanArch.databinding.FragmentTaskListBinding
import com.geeks.cleanArch.presentation.fragments.adapter.TaskAdapter
import com.geeks.cleanArch.presentation.fragments.TaskViewModel
import com.geeks.cleanArch.presentation.model.TaskUI
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private val binding by viewBinding(FragmentTaskListBinding::bind)
    private val viewModel: TaskViewModel by viewModel()
    private val taskAdapter = TaskAdapter(emptyList(), ::onItemClick, ::onTaskDelete)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadTasks()
        addTask()
        initialize()
        showTask()
    }

    private fun addTask() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddTaskActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    private fun initialize() {
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

    private fun onItemClick(id: Int) {
        viewModel.viewModelScope.launch {
            viewModel.getTask(id)
        }
        val action = TaskListFragmentDirections.actionTaskListFragmentToDetailFragment(id)
        findNavController().navigate(action)
    }

    private fun onTaskDelete(taskUI: TaskUI) {
        viewModel.deleteTask(taskUI)
    }
}