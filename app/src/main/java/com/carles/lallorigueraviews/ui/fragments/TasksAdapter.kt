package com.carles.lallorigueraviews.ui.fragments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carles.lallorigueraviews.databinding.ItemTascBinding
import com.carles.lallorigueraviews.model.Tasc
import com.carles.lallorigueraviews.ui.common.setDebounceClickListener
import com.google.android.material.color.MaterialColors
import kotlin.math.absoluteValue

class TasksAdapter(
    private val onTaskDone: (Tasc) -> Unit,
    private val onEditTask: (Tasc) -> Unit
) : ListAdapter<Tasc, TasksAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTascBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTascBinding) : RecyclerView.ViewHolder(binding.root) {

        private val delayedTaskColor by lazy {
            MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorErrorContainer)
        }

        init {
            binding.taskEditButton.setDebounceClickListener { onEditTask(getItem(layoutPosition)) }
            binding.taskMarkAsDoneButton.setDebounceClickListener { onTaskDone(getItem(layoutPosition)) }
        }

        fun bind(task: Tasc) {
            binding.taskCard.setCardBackgroundColor(
                if (task.daysRemaining < 0) delayedTaskColor else Color.TRANSPARENT
            )
            binding.taskNameText.text = task.name
            binding.taskDaysRemainingText.text = task.daysRemaining.absoluteValue.toString()
            binding.taskOneTimeText.isVisible = task.isOneTime
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tasc>() {
            override fun areItemsTheSame(oldItem: Tasc, newItem: Tasc): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Tasc, newItem: Tasc): Boolean {
                return oldItem == newItem
            }
        }
    }
}