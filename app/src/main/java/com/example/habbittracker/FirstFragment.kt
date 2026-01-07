package com.example.habbittracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habbittracker.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory((requireActivity().application as HabitTrackerApplication).repository)
    }
    private lateinit var taskAdapter: TaskAdapter
    private var currentFilter: TaskStatus = TaskStatus.TODO
    private var currentSortOrder: SortOrder = SortOrder.NONE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupSortButton()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList(), 
            onTaskCompleted = { task ->
                // Toggle task completion status
                if (task.isCompleted) {
                    taskViewModel.markTaskAsPending(task)
                } else {
                    taskViewModel.markTaskAsCompleted(task)
                }
            },
            onDeleteTask = { task ->
                taskViewModel.deleteTask(task)
            }
        )
        
        binding.recyclerviewTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    private fun setupSortButton() {
        binding.buttonSort.setOnClickListener {
            // Cycle through sort options
            currentSortOrder = when (currentSortOrder) {
                SortOrder.NONE -> SortOrder.BY_PRIORITY
                SortOrder.BY_PRIORITY -> SortOrder.BY_DATE
                SortOrder.BY_DATE -> SortOrder.BY_TITLE
                SortOrder.BY_TITLE -> SortOrder.NONE
            }
            updateHeaderText(currentFilter)
            val currentTasks = taskViewModel.tasks.value ?: emptyList()
            applyFilter(currentTasks)
        }
    }

    private fun observeViewModel() {
        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            applyFilter(tasks)
        }
    }

    fun filterTasks(status: TaskStatus) {
        currentFilter = status
        updateHeaderText(status)
        val currentTasks = taskViewModel.tasks.value ?: emptyList()
        applyFilter(currentTasks)
    }

    private fun updateHeaderText(status: TaskStatus) {
        val statusText = when(status) {
            TaskStatus.TODO -> getString(R.string.nav_todo)
            TaskStatus.PENDING -> getString(R.string.nav_pending)
            TaskStatus.COMPLETED -> getString(R.string.nav_completed)
        }
        
        val sortText = when (currentSortOrder) {
            SortOrder.NONE -> ""
            SortOrder.BY_PRIORITY -> " (Sorted by Priority)"
            SortOrder.BY_DATE -> " (Sorted by Date)"
            SortOrder.BY_TITLE -> " (Sorted by Title)"
        }
        
        binding.textViewHeader.text = "$statusText$sortText"
    }

    private fun applyFilter(tasks: List<Task>) {
        val filteredTasks = tasks.filter { it.status == currentFilter }
        val sortedTasks = when (currentSortOrder) {
            SortOrder.NONE -> filteredTasks
            SortOrder.BY_PRIORITY -> filteredTasks.sortedBy { getPriorityValue(it.priority) }
            SortOrder.BY_DATE -> filteredTasks.sortedBy { it.createdDate }
            SortOrder.BY_TITLE -> filteredTasks.sortedBy { it.title.lowercase() }
        }
        taskAdapter.updateTasks(sortedTasks)
    }

    private fun getPriorityValue(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class SortOrder {
    NONE,
    BY_PRIORITY,
    BY_DATE,
    BY_TITLE
}