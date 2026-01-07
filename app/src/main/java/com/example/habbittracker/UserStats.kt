package com.example.habbittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val totalXp: Int = 0,
    val level: Int = 1
)
