package com.example.domain.usecase

import com.example.domain.model.TaskModel
import com.example.domain.repository.TaskManagerRepository
import java.time.LocalDateTime
import java.util.Calendar

class InsertTaskUseCase(private val taskManagerRepository: TaskManagerRepository) {

    suspend fun insertTask(taskModel: TaskModel, versionSdk: Int): String {
        val existingTask = taskManagerRepository.getTaskByName(taskModel.taskName)
        if (existingTask != null) {
            return "Task with the same name already exists."
        }

        val taskDate = taskModel.taskDate.toIntOrNull()
        val currentHour: Int = if (versionSdk >= VERSION_CODES_0) {
            LocalDateTime.now().hour
        } else {
            val calendar = Calendar.getInstance()
            calendar.get(Calendar.HOUR_OF_DAY)
        }

        if (taskDate == null || taskDate < currentHour) {
            return "Invalid task date. Task date must be in the future."
        }
        taskManagerRepository.insertTask(taskModel)
        return "Task added successfully"
    }
}

private const val VERSION_CODES_0 = 26