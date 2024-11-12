package com.geeks.cleanArch.domain.usecase

import com.geeks.cleanArch.domain.model.TaskModel
import com.geeks.cleanArch.domain.repository.TaskManagerRepository

class UpdateTaskUseCase(private val taskManagerRepository: TaskManagerRepository) {

    suspend fun updateTask(taskModel: TaskModel) {
        taskManagerRepository.updateTask(taskModel)
    }
}