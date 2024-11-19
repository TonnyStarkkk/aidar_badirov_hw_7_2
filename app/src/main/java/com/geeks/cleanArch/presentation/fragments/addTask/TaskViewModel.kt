package com.geeks.cleanArch.presentation.fragments.addTask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.geeks.cleanArch.domain.usecase.DeleteTaskUseCase
import com.geeks.cleanArch.domain.usecase.GetAllTasksUseCase
import com.geeks.cleanArch.domain.usecase.GetTaskUseCase
import com.geeks.cleanArch.domain.usecase.InsertTaskUseCase
import com.geeks.cleanArch.domain.usecase.UpdateTaskUseCase
import com.geeks.cleanArch.presentation.model.TaskUI
import com.geeks.cleanArch.presentation.model.toDomain
import com.geeks.cleanArch.presentation.model.toUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TaskViewModel(
    private val insertTaskUseCase: InsertTaskUseCase,
    private val getAllTaskUseCase: GetAllTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    private val _tasksStateFlow = MutableStateFlow<List<TaskUI>>(emptyList())
    val tasksFlow: StateFlow<List<TaskUI>> = _tasksStateFlow.asStateFlow()

    private val _taskStateFlow = MutableStateFlow<TaskUI?>(null)
    val taskStateFlow: StateFlow<TaskUI?> = _taskStateFlow.asStateFlow()

    private val _insertMessageStateFlow = MutableStateFlow(String())
    val insertMessageFlow: StateFlow<String> = _insertMessageStateFlow.asStateFlow()

    private val _updateMessageStateFlow = MutableStateFlow(String())
    val updateMessageFlow: StateFlow<String> = _updateMessageStateFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow<LoadingState>(LoadingState.Loaded)
    val loadingFlow: StateFlow<LoadingState> = _loadingStateFlow.asStateFlow()

    private val _errorMessageFlow = MutableStateFlow(String())
    val errorMessageFlow: StateFlow<String> = _errorMessageFlow.asStateFlow()

    private fun <T> runLaunchIO(block: suspend CoroutineScope.() -> T) {
        _loadingStateFlow.value = LoadingState.Loading
        try {
            viewModelScope.launch(Dispatchers.IO) {
                block()
            }
        } catch (e: Exception) {
            _errorMessageFlow.value = "Error: ${e.localizedMessage} "
        } finally {
            _loadingStateFlow.value = LoadingState.Loaded
        }
    }

    fun loadTask(id: Int) {
        viewModelScope.launch {
            _loadingStateFlow.value = LoadingState.Loading
            try {
                val task = getTaskUseCase(id)?.toUI()
                _taskStateFlow.value = task
            } catch (e: Exception) {
                _errorMessageFlow.value = "Error: ${e.localizedMessage} "
            }finally {
                _loadingStateFlow.value = LoadingState.Loaded
            }
        }
    }

    fun insertTask(taskUI: TaskUI) {
        runLaunchIO {
            val message = insertTaskUseCase.insertTask(taskUI.toDomain())
            _insertMessageStateFlow.value = message
        }
    }

    fun loadTasks() {
        runLaunchIO {
            getAllTaskUseCase().onEach { tasks ->
                _tasksStateFlow.value = tasks.map { taskModel -> taskModel.toUI() }
            }.collect()
        }
    }

    suspend fun getTask(id: Int) = getTaskUseCase(id)?.toUI()

    fun updateTask(taskUI: TaskUI) {
        runLaunchIO {
            updateTaskUseCase.updateTask(taskUI.toDomain())
            _updateMessageStateFlow.value =
                LoadingState.Error("Task updated successfully").toString()
        }
    }

    fun deleteTask(taskUI: TaskUI) {
        runLaunchIO {
            deleteTaskUseCase.deleteTask(taskUI.toDomain())
            loadTasks()
        }
    }
}

sealed class LoadingState {
    data object Loading : LoadingState()
    data object Loaded : LoadingState()
    data class Error(val message: String) : LoadingState()
}