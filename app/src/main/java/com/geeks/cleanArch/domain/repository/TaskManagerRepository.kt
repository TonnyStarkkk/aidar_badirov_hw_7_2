package com.geeks.cleanArch.domain.repository

import com.geeks.cleanArch.domain.model.TaskModel

interface TaskManagerRepository {

    suspend fun insertTask(taskModel: TaskModel)
    suspend fun getAllTasks(): List<TaskModel>
    suspend fun getTaskByName(taskName: String): TaskModel?


}