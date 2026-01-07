package com.example.habbittracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class TaskStatus {
    TODO, COMPLETED, PENDING
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: String = "",
    val priority: Priority,
    val schedule: String,
    var isCompleted: Boolean = false,
    var status: TaskStatus = TaskStatus.TODO,
    val createdDate: Long = System.currentTimeMillis(),
    val xpValue: Int = 10
)