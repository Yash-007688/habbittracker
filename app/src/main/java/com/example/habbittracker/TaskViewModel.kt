package com.example.habbittracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Calendar

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: LiveData<List<Task>> = repository.allTasks.asLiveData()
    val userStats: LiveData<UserStats?> = repository.userStats.asLiveData()

    fun checkAndMovePendingTasks() {
        val currentTasks = tasks.value.orEmpty()
        val todayStart = getStartOfDay(System.currentTimeMillis())

        viewModelScope.launch {
            currentTasks.forEach { task ->
                val taskDateStart = getStartOfDay(task.createdDate)
                if (taskDateStart < todayStart && task.status == TaskStatus.TODO) {
                    repository.update(task.copy(status = TaskStatus.PENDING))
                }
            }
        }
    }

    private fun getStartOfDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun addTask(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun markTaskAsCompleted(task: Task) = viewModelScope.launch {
        repository.update(task.copy(status = TaskStatus.COMPLETED, isCompleted = true))
        repository.addXp(task.xpValue)
    }
    
    fun markTaskAsPending(task: Task) = viewModelScope.launch {
        repository.update(task.copy(status = TaskStatus.PENDING, isCompleted = false))
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}
