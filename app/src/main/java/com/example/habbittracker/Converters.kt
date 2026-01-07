package com.example.habbittracker

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priorityString: String): Priority {
        return Priority.valueOf(priorityString)
    }

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(statusString: String): TaskStatus {
        return TaskStatus.valueOf(statusString)
    }
}
