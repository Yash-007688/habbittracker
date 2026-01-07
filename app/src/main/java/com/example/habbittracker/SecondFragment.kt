package com.example.habbittracker

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.habbittracker.databinding.FragmentSecondBinding
import java.util.Calendar
import java.util.Locale

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory((requireActivity().application as HabitTrackerApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextSchedule.setOnClickListener {
            showTimePicker()
        }

        binding.buttonSave.setOnClickListener {
            saveTask()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val timeFormat = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )
                // Optional: Convert to AM/PM format
                val isAm = selectedHour < 12
                val amPm = if (isAm) "AM" else "PM"
                val displayHour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, selectedMinute, amPm)
                
                binding.editTextSchedule.setText(formattedTime)
            },
            hour,
            minute,
            false // Use 12-hour format
        )
        timePickerDialog.show()
    }

    private fun saveTask() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()
        val schedule = binding.editTextSchedule.text.toString()
        val category = binding.editTextCategory.text.toString()
        
        if (title.isBlank()) {
            binding.textInputLayoutTitle.error = "Title is required"
            return
        } else {
            binding.textInputLayoutTitle.error = null
        }

        val priority = when (binding.radioGroupPriority.checkedRadioButtonId) {
            R.id.radioButtonHigh -> Priority.HIGH
            R.id.radioButtonMedium -> Priority.MEDIUM
            R.id.radioButtonLow -> Priority.LOW
            else -> Priority.MEDIUM // Default
        }

        val task = Task(
            title = title,
            description = description,
            category = category,
            schedule = if (schedule.isBlank()) "Anytime" else schedule,
            priority = priority,
            status = TaskStatus.TODO,
            isCompleted = false
        )

        taskViewModel.addTask(task)
        Toast.makeText(context, "Task created successfully!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}