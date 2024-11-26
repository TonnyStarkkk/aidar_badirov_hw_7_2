package com.geeks.data.repositoryImpl

import com.example.domain.model.TaskModel
import com.example.domain.repository.TaskManagerRepository
import com.example.domain.result.Result
import com.geeks.data.database.dao.TaskManagerDao
import com.geeks.data.dto.toData
import com.geeks.data.dto.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskManagerRepositoryImpl(
    private val taskManagerDao: TaskManagerDao
) : TaskManagerRepository {
    override suspend fun getTask(id: Int): Result<TaskModel> {
        try {
            val data = taskManagerDao.getTaskById(id)
            return Result.Success(data.toDomain())
        } catch (ex: Exception) {
            return Result.Error(ex.message.toString())
        }
    }

    override suspend fun insertTask(taskModel: TaskModel): Result<TaskModel> {
        try {
            taskManagerDao.insertTask(taskModel.toData())
            return Result.Success(taskModel)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
            return Result.Error(e.message.toString())
        }
    }

    override suspend fun getAllTasks(): Flow<Result<List<TaskModel>>> {
        return taskManagerDao.getAllTasks().map { list ->
            try {
                Result.Success(list.map { dto -> dto.toDomain() })
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
        }
    }

    override suspend fun getTaskByName(taskName: String): Result<TaskModel> {
        try {
            val task = taskManagerDao.getTaskByName(taskName).toDomain()
            return Result.Success(task)
        } catch (e: Exception) {
            return Result.Error(e.message.toString())
        }
    }

    override suspend fun updateTask(taskModel: TaskModel) : Result<TaskModel> {
        try {
            taskManagerDao.updateTask(taskModel.toData())
            return Result.Success(taskModel)
        } catch (e: Exception) {
            return Result.Error(e.message.toString())
        }
    }

    override suspend fun deleteTask(task: TaskModel): Result<TaskModel> {
        try {
            taskManagerDao.deleteTask(task.toData())
            return Result.Success(task)
        } catch (e: Exception) {
            return Result.Error(e.message.toString())
        }
    }
}