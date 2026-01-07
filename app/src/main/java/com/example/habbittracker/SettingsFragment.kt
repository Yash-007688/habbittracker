package com.example.habbittracker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import com.example.habbittracker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    
    private val taskViewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory((requireActivity().application as HabitTrackerApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = requireContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        
        setupThemeOptions()
        setupColorOptions()
        setupNotificationOptions()
        setupDataManagement()
    }

    private fun setupThemeOptions() {
        val selectedTheme = sharedPreferences.getInt("selected_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        when (selectedTheme) {
            AppCompatDelegate.MODE_NIGHT_YES -> binding.radioButtonDark.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> binding.radioButtonLight.isChecked = true
            else -> binding.radioButtonSystem.isChecked = true
        }
        
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val newThemeMode = when (checkedId) {
                R.id.radioButtonLight -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.radioButtonDark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            
            AppCompatDelegate.setDefaultNightMode(newThemeMode)
            sharedPreferences.edit().putInt("selected_theme", newThemeMode).apply()
        }
    }

    private fun setupColorOptions() {
        val selectedColor = sharedPreferences.getString("selected_color", "purple")
        
        // Initially highlight the selected color
        highlightSelectedColor(selectedColor ?: "purple")

        val colorOptions = mapOf(
            binding.colorOption1 to "purple",
            binding.colorOption2 to "teal",
            binding.colorOption3 to "red",
            binding.colorOption4 to "orange",
            binding.colorOption5 to "green"
        )

        colorOptions.forEach { (view, colorName) ->
            view.setOnClickListener {
                if (sharedPreferences.getString("selected_color", "purple") != colorName) {
                    sharedPreferences.edit().putString("selected_color", colorName).apply()
                    highlightSelectedColor(colorName)
                    // Recreate activity to apply new theme
                    requireActivity().recreate()
                }
            }
        }
    }

    private fun highlightSelectedColor(colorName: String) {
        val colorViews = listOf(
            binding.colorOption1,
            binding.colorOption2,
            binding.colorOption3,
            binding.colorOption4,
            binding.colorOption5
        )
        
        val selectedIndex = when (colorName) {
            "purple" -> 0
            "teal" -> 1
            "red" -> 2
            "orange" -> 3
            "green" -> 4
            else -> 0
        }
        
        colorViews.forEachIndexed { index, view ->
            if (index == selectedIndex) {
                view.setBackgroundResource(R.drawable.color_selection_border)
            } else {
                view.setBackgroundResource(R.drawable.circle_shape)
            }
        }
    }

    private fun setupNotificationOptions() {
        val remindersEnabled = sharedPreferences.getBoolean("reminders_enabled", true)
        binding.switchReminders.isChecked = remindersEnabled
        
        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("reminders_enabled", isChecked).apply()
        }
    }

    private fun setupDataManagement() {
        binding.btnClearData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to delete all tasks, streaks, and XP? This action cannot be undone.")
                .setPositiveButton("Clear Data") { _, _ ->
                    taskViewModel.deleteAll()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
