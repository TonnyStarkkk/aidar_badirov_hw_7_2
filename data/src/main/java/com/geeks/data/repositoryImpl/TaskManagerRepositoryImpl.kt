package com.geeks.data.repositoryImpl

import com.example.domain.model.TaskModel
import com.example.domain.repository.TaskManagerRepository
import com.geeks.data.database.dao.TaskManagerDao
import com.geeks.data.dto.toData
import com.geeks.data.dto.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskManagerRepositoryImpl(
    private val taskManagerDao: TaskManagerDao
) : TaskManagerRepository {
    override suspend fun getTask(id: Int): TaskModel {
        val taskDto = taskManagerDao.getTaskById(id)
        return taskDto.toDomain()
    }

    override suspend fun insertTask(taskModel: TaskModel) {
        return taskManagerDao.insertTask(taskModel.toData())
    }

    override suspend fun getAllTasks(): Flow<List<TaskModel>> {
        return taskManagerDao.getAllTasks().map { list ->
            list.map { dto ->
                dto.toDomain()
            }
        }
    }

    override suspend fun getTaskByName(taskName: String): TaskModel? {
        return taskManagerDao.getTaskByName(taskName)?.toDomain()
    }

    override suspend fun updateTask(taskModel: TaskModel) {
        return taskManagerDao.updateTask(taskModel.toData())
    }

    override suspend fun deleteTask(task: TaskModel) {
        return taskManagerDao.deleteTask(task.toData())
    }
}