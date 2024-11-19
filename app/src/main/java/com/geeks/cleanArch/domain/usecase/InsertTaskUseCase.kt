package com.geeks.cleanArch.domain.usecase

import android.os.Build
import android.util.Log
import com.geeks.cleanArch.domain.model.TaskModel
import com.geeks.cleanArch.domain.repository.TaskManagerRepository
import java.time.LocalDateTime
import java.util.Calendar

class InsertTaskUseCase(private val taskManagerRepository: TaskManagerRepository) {

    suspend fun insertTask(taskModel: TaskModel): String {
        val existingTask = taskManagerRepository.getTaskByName(taskModel.taskName)
        if (existingTask != null) {
            return "Task with the same name already exists."
        }

        val taskDate = taskModel.taskDate.toIntOrNull() ?: run {
            Log.e("TaskDateConversion", "Invalid taskDate: ${taskModel.taskDate}")
            0
        }
        val currentHour: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().hour
        } else {
            val calendar = Calendar.getInstance()
            calendar.get(Calendar.HOUR_OF_DAY)
        }

        if (taskDate < currentHour) {
            return "Invalid task date. Task date must be in the future."
        }
        taskManagerRepository.insertTask(taskModel)
        return "Task added successfully"
    }
}