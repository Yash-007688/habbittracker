package com.example.habbittracker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskCompleted: (Task) -> Unit,
    private val onDeleteTask: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCard: CardView = itemView.findViewById(R.id.taskCard)
        val title: TextView = itemView.findViewById(R.id.textViewTitle)
        val schedule: TextView = itemView.findViewById(R.id.textViewSchedule)
        val description: TextView = itemView.findViewById(R.id.textViewDescription)
        val category: TextView = itemView.findViewById(R.id.textViewCategory)
        val priorityIndicator: View = itemView.findViewById(R.id.viewPriorityIndicator)
        val deleteButton: ImageView = itemView.findViewById(R.id.buttonDelete)
        val checkboxTask: CheckBox = itemView.findViewById(R.id.checkboxTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.schedule.text = task.schedule

        // Set description if available
        if (task.description.isNotBlank()) {
            holder.description.text = task.description
            holder.description.visibility = View.VISIBLE
        } else {
            holder.description.visibility = View.GONE
        }

        // Set category if available
        if (task.category.isNotBlank()) {
            holder.category.text = task.category
            holder.category.visibility = View.VISIBLE
        } else {
            holder.category.visibility = View.GONE
        }

        // Set checkbox state based on task completion
        holder.checkboxTask.isChecked = task.isCompleted

        // Update card appearance based on completion status
        if (task.isCompleted) {
            holder.title.alpha = 0.6f
            holder.schedule.alpha = 0.6f
            holder.description.alpha = 0.6f
            holder.title.paint.isStrikeThruText = true
        } else {
            holder.title.alpha = 1.0f
            holder.schedule.alpha = 1.0f
            holder.description.alpha = 1.0f
            holder.title.paint.isStrikeThruText = false
        }

        // Set priority indicator color
        val colorRes = when (task.priority) {
            Priority.HIGH -> R.color.priority_high
            Priority.MEDIUM -> R.color.priority_medium
            Priority.LOW -> R.color.priority_low
        }
        holder.priorityIndicator.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(holder.itemView.context, colorRes)
        )

        // Set delete button
        holder.deleteButton.setOnClickListener {
            // Add animation to deletion
            val scaleDown = ObjectAnimator.ofFloat(holder.taskCard, "scaleX", 1.0f, 0.0f)
            scaleDown.duration = 300
            scaleDown.start()
            
            scaleDown.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onDeleteTask(task)
                }
            })
        }

        // Set click listener for the entire card to mark task as completed
        holder.taskCard.setOnClickListener {
            animateTaskCompletion(holder, task)
            onTaskCompleted(task)
        }

        // Also allow checkbox click to mark as completed (for accessibility)
        holder.checkboxTask.setOnClickListener {
            animateTaskCompletion(holder, task)
            onTaskCompleted(task)
        }
    }

    private fun animateTaskCompletion(holder: TaskViewHolder, task: Task) {
        if (task.isCompleted) {
            // Task is being uncompleted - scale down
            val scaleDown = ObjectAnimator.ofFloat(holder.taskCard, "scaleX", 1.0f, 0.95f)
            val scaleUp = ObjectAnimator.ofFloat(holder.taskCard, "scaleX", 0.95f, 1.0f)
            
            val animatorSet = AnimatorSet().apply {
                playSequentially(scaleDown, scaleUp)
                duration = 300
            }
            animatorSet.start()
        } else {
            // Task is being completed - add checkmark animation
            val scaleDown = ObjectAnimator.ofFloat(holder.taskCard, "scaleX", 1.0f, 0.95f)
            val scaleUp = ObjectAnimator.ofFloat(holder.taskCard, "scaleX", 0.95f, 1.0f)
            
            val animatorSet = AnimatorSet().apply {
                playSequentially(scaleDown, scaleUp)
                duration = 300
            }
            animatorSet.start()
        }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}