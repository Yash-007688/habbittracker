package com.example.habbittracker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class HabitTrackerApplication : Application() {
    
    val database by lazy { TaskDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao(), database.userStatsDao()) }

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_MODE = "selected_theme"
        private const val KEY_ACCENT_COLOR = "selected_color"
        
        fun setThemeFromPrefs(context: Context) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val themeMode = sharedPreferences.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            AppCompatDelegate.setDefaultNightMode(themeMode)
            
            // Apply accent color theme
            val selectedColor = sharedPreferences.getString(KEY_ACCENT_COLOR, "purple")
            val themeId = when (selectedColor) {
                "teal" -> R.style.Theme_HabbitTracker_Teal
                "red" -> R.style.Theme_HabbitTracker_Red
                "orange" -> R.style.Theme_HabbitTracker_Orange
                "green" -> R.style.Theme_HabbitTracker_Green
                else -> R.style.Theme_HabbitTracker
            }
            context.setTheme(themeId)
        }
    }

    override fun onCreate() {
        super.onCreate()
        setThemeFromPrefs(this)
    }
}
