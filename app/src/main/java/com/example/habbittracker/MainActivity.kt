package com.example.habbittracker

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.habbittracker.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((application as HabitTrackerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme before setting content view
        HabitTrackerApplication.setThemeFromPrefs(this)
        
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navView.setNavigationItemSelectedListener(this)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        // setupActionBarWithNavController(navController, appBarConfiguration) // Removed to use custom toggle

        // Regular click for custom task
        binding.fab.setOnClickListener { view ->
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        
        // Long click for quick actions
        binding.fab.setOnLongClickListener {
            showQuickActionsDialog()
            true
        }
        
        // Hide FAB on specific fragments, but keep bottom navigation visible on all pages
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.SecondFragment || 
                destination.id == R.id.NotesFragment || 
                destination.id == R.id.TimerFragment || 
                destination.id == R.id.StopwatchFragment ||
                destination.id == R.id.AlarmFragment ||
                destination.id == R.id.SettingsFragment) {
                binding.fab.hide()
                // Keep bottom navigation visible on all pages
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                binding.fab.show()
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_menu) {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                return@setOnItemSelectedListener false // Don't select the item
            }
            
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.action_FirstFragment_to_DashboardFragment)
                }
                else -> {
                    val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)?.childFragmentManager?.fragments?.firstOrNull()
                    if (fragment is FirstFragment) {
                        when (item.itemId) {
                            R.id.navigation_todo -> fragment.filterTasks(TaskStatus.TODO)
                            R.id.navigation_pending -> fragment.filterTasks(TaskStatus.PENDING)
                            R.id.navigation_completed -> fragment.filterTasks(TaskStatus.COMPLETED)
                        }
                    }
                }
            }
            true
        }
    }

    private fun showQuickActionsDialog() {
        // Apply current theme to dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_quick_tasks)
        dialog.setCancelable(true)
        
        dialog.findViewById<View>(R.id.btnQuickHabit).setOnClickListener {
            val task = Task(
                title = "Daily Exercise",
                description = "Complete your daily exercise routine",
                category = "Health",
                priority = Priority.HIGH,
                schedule = "07:00 AM",
                status = TaskStatus.TODO,
                isCompleted = false
            )
            taskViewModel.addTask(task)
            Toast.makeText(this, "Quick task added: Daily Exercise", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        dialog.findViewById<View>(R.id.btnQuickTask).setOnClickListener {
            val task = Task(
                title = "Morning Routine",
                description = "Complete your morning routine tasks",
                category = "Personal",
                priority = Priority.MEDIUM,
                schedule = "08:00 AM",
                status = TaskStatus.TODO,
                isCompleted = false
            )
            taskViewModel.addTask(task)
            Toast.makeText(this, "Quick task added: Morning Routine", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        dialog.findViewById<View>(R.id.btnQuickReminder).setOnClickListener {
            val task = Task(
                title = "Evening Review",
                description = "Review your day and plan for tomorrow",
                category = "Personal",
                priority = Priority.LOW,
                schedule = "08:00 PM",
                status = TaskStatus.TODO,
                isCompleted = false
            )
            taskViewModel.addTask(task)
            Toast.makeText(this, "Quick task added: Evening Review", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        dialog.findViewById<View>(R.id.btnCustomTask).setOnClickListener {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
            dialog.dismiss()
        }
        
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        // Check for tasks that need to be moved to pending when the app is opened/resumed
        taskViewModel.checkAndMovePendingTasks()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        
        when (item.itemId) {
            R.id.nav_notes -> {
                navController.navigate(R.id.action_FirstFragment_to_NotesFragment)
            }
            R.id.nav_timer -> {
                navController.navigate(R.id.action_FirstFragment_to_TimerFragment)
            }
            R.id.nav_alarm -> {
                 navController.navigate(R.id.action_FirstFragment_to_AlarmFragment)
            }
            R.id.nav_stopwatch -> {
                 navController.navigate(R.id.action_FirstFragment_to_StopwatchFragment)
            }
            R.id.nav_settings -> {
                navController.navigate(R.id.action_FirstFragment_to_SettingsFragment)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}