package com.example.habbittracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.habbittracker.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory((requireActivity().application as HabitTrackerApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        
        // Set up quick action buttons
        binding.btnAddHabit.setOnClickListener {
            // Navigate to add habit screen
            // For now, just show a toast
        }
        
        binding.btnAddReminder.setOnClickListener {
            // Navigate to add reminder screen
            // For now, just show a toast
        }
    }

    private fun observeViewModel() {
        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            updateDashboardStats(tasks)
            updateWeeklyChart(tasks)
        }
        
        taskViewModel.userStats.observe(viewLifecycleOwner) { stats ->
            stats?.let { updateLevelUI(it) }
        }
    }

    private fun updateLevelUI(stats: UserStats) {
        binding.textLevelLabel.text = "Level ${stats.level}"
        val xpInCurrentLevel = stats.totalXp % 100
        binding.textXpLabel.text = "$xpInCurrentLevel / 100 XP"
        binding.progressXp.progress = xpInCurrentLevel
    }

    private fun updateDashboardStats(tasks: List<Task>) {
        // Calculate and display statistics
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.isCompleted }
        val completionRate = if (totalTasks > 0) {
            (completedTasks.toFloat() / totalTasks * 100).toInt()
        } else 0

        binding.textTotalTasks.text = totalTasks.toString()
        binding.textCompletedTasks.text = completedTasks.toString()
        binding.textCompletionRate.text = "${completionRate}%"

        // Update streaks (simplified calculation)
        // In a real app, you'd calculate actual streaks based on completion dates
        binding.textCurrentStreak.text = "5"
        binding.textBestStreak.text = "12"
    }

    private fun updateWeeklyChart(tasks: List<Task>) {
        // For demo purposes, we'll simulate weekly completion data
        // In a real app, you would calculate this based on actual task completion dates
        val completedTasks = tasks.count { it.isCompleted }
        
        // Update the chart bars based on completion data
        val maxBarHeight = 150 // Maximum height for the bars
        val totalTasks = tasks.size
        
        if (totalTasks > 0) {
            val completionRatio = completedTasks.toFloat() / totalTasks
            val barHeight = (completionRatio * maxBarHeight).toInt()
            
            // Update each day's bar (in a real app, you'd calculate per day)
            // Accessing views through the included layout 'weeklyChart'
            val layoutParamsMon = binding.weeklyChart.barMon.layoutParams
            layoutParamsMon.height = if (barHeight > 0) barHeight else 20
            binding.weeklyChart.barMon.layoutParams = layoutParamsMon
            
            val layoutParamsTue = binding.weeklyChart.barTue.layoutParams
            layoutParamsTue.height = if (barHeight > 10) barHeight - 10 else 20
            binding.weeklyChart.barTue.layoutParams = layoutParamsTue
            
            val layoutParamsWed = binding.weeklyChart.barWed.layoutParams
            layoutParamsWed.height = if (barHeight > 20) barHeight - 20 else 20
            binding.weeklyChart.barWed.layoutParams = layoutParamsWed
            
            val layoutParamsThu = binding.weeklyChart.barThu.layoutParams
            layoutParamsThu.height = if (barHeight > 5) barHeight - 5 else 20
            binding.weeklyChart.barThu.layoutParams = layoutParamsThu
            
            val layoutParamsFri = binding.weeklyChart.barFri.layoutParams
            layoutParamsFri.height = if (barHeight > 15) barHeight - 15 else 20
            binding.weeklyChart.barFri.layoutParams = layoutParamsFri
            
            val layoutParamsSat = binding.weeklyChart.barSat.layoutParams
            layoutParamsSat.height = if (barHeight > 30) barHeight - 30 else 20
            binding.weeklyChart.barSat.layoutParams = layoutParamsSat
            
            val layoutParamsSun = binding.weeklyChart.barSun.layoutParams
            layoutParamsSun.height = if (barHeight > 40) barHeight - 40 else 20
            binding.weeklyChart.barSun.layoutParams = layoutParamsSun
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}