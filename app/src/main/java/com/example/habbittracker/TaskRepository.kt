package com.example.habbittracker

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao, private val userStatsDao: UserStatsDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val userStats: Flow<UserStats?> = userStatsDao.getUserStats()

    suspend fun addXp(amount: Int) {
        userStatsDao.addXp(amount)
    }

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteAll() {
        taskDao.deleteAllTasks()
    }
}
