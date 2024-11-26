package com.geeks.cleanArch.presentation.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.result.Result
import com.example.domain.usecase.DeleteTaskUseCase
import com.example.domain.usecase.GetAllTasksUseCase
import com.example.domain.usecase.GetTaskUseCase
import com.example.domain.usecase.InsertTaskUseCase
import com.example.domain.usecase.UpdateTaskUseCase
import com.geeks.cleanArch.presentation.model.TaskUI
import com.geeks.cleanArch.presentation.model.toDomain
import com.geeks.cleanArch.presentation.model.toUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(
    private val insertTaskUseCase: InsertTaskUseCase,
    private val getAllTaskUseCase: GetAllTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    private val _tasksStateFlow = MutableStateFlow<List<TaskUI>>(emptyList())
    val tasksFlow: StateFlow<List<TaskUI>> = _tasksStateFlow.asStateFlow()

    private val _taskStateFlow = MutableStateFlow<Result<TaskUI>>(Result.Loading)
    val taskStateFlow: StateFlow<Result<TaskUI>> = _taskStateFlow.asStateFlow()

    private val _insertMessageStateFlow = MutableStateFlow(String())
    val insertMessageFlow: StateFlow<String> = _insertMessageStateFlow.asStateFlow()

    private val _updateMessageStateFlow = MutableStateFlow(String())
    val updateMessageFlow: StateFlow<String> = _updateMessageStateFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow<LoadingState>(LoadingState.Loaded)
    val loadingFlow: StateFlow<LoadingState> = _loadingStateFlow.asStateFlow()

    private val _errorMessageFlow = MutableStateFlow(String())
    val errorMessageFlow: StateFlow<String> = _errorMessageFlow.asStateFlow()

    private fun <T> runLaunchIO(block: suspend CoroutineScope.() -> T) {
        viewModelScope.launch {
            _loadingStateFlow.value = LoadingState.Loading
            try {
                withContext(Dispatchers.IO) { block() }
            } catch (e: Exception) {
                _errorMessageFlow.value = "Error: ${e.localizedMessage} "
            } finally {
                _loadingStateFlow.value = LoadingState.Loaded
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAllTaskUseCase().collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _tasksStateFlow.value = emptyList()
                        }

                        is Result.Success -> {
                            _tasksStateFlow.value = result.data.map { it.toUI() }

                        }

                        is Result.Error -> {
                            _errorMessageFlow.value = result.message
                        }
                    }
                }
            } catch (e: Exception) {
                Result.Error(e.localizedMessage ?: "Error")
            }
        }
    }



    fun getTask(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = getTaskUseCase(id)) {

                is Result.Loading -> {
                    _taskStateFlow.value = Result.Loading
                }

                is Result.Success -> {
                    _taskStateFlow.value = Result.Success(result.data.toUI())
                }

                is Result.Error -> {
                    _taskStateFlow.value = Result.Error(result.message)
                }
            }
        }
    }

    fun saveTask(taskUI: TaskUI, versionSdk: Int) {
        runLaunchIO {
            when (val result = insertTaskUseCase.insertTask(taskUI.toDomain(), versionSdk)) {
                is Result.Loading -> {
                    _taskStateFlow.value = Result.Loading
                }

                is Result.Success -> {
                    _taskStateFlow.value = Result.Success(result.data.toUI())

                }

                is Result.Error -> {
                    _taskStateFlow.value = Result.Error(result.message)
                }
            }
        }
    }

    fun updateTask(taskUI: TaskUI) {
        runLaunchIO {
            when (val result = updateTaskUseCase.updateTask(taskUI.toDomain())) {

                is Result.Loading -> {
                    _taskStateFlow.value = Result.Loading
                }

                is Result.Success -> {
                    _taskStateFlow.value = Result.Success(result.data.toUI())
                    loadTasks()
                }

                is Result.Error -> {
                    _taskStateFlow.value = Result.Error(result.message)
                }
            }
        }
    }

    fun deleteTask(taskUI: TaskUI) {
        runLaunchIO {
            when (val result = deleteTaskUseCase.deleteTask(taskUI.toDomain())) {
                is Result.Loading -> {
                    _taskStateFlow.value = Result.Loading
                }

                is Result.Success -> {
                    _taskStateFlow.value = Result.Success(result.data.toUI())
                    loadTasks()
                }

                is Result.Error -> {
                    _taskStateFlow.value = Result.Error(result.message)

                }
            }
        }
    }

    sealed class LoadingState {
        data object Loading : LoadingState()
        data object Loaded : LoadingState()
        data class Error(val message: String) : LoadingState()
    }
}