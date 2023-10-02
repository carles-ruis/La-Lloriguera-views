package com.carles.lallorigueraviews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.carles.lallorigueraviews.MainActivity
import com.carles.lallorigueraviews.Navigator
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.databinding.FragmentTasksBinding
import com.carles.lallorigueraviews.model.Tasc
import com.carles.lallorigueraviews.ui.common.setDebounceClickListener
import com.carles.lallorigueraviews.ui.common.showToast
import com.carles.lallorigueraviews.ui.viewmodel.TasksEvent
import com.carles.lallorigueraviews.ui.viewmodel.TasksState
import com.carles.lallorigueraviews.ui.viewmodel.TasksViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TasksFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeState()
        observeEvents()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initViews() {
        binding.tasksRecyclerview.adapter = TasksAdapter(
            onTaskDone = { task -> viewModel.onTaskDone(task) },
            onEditTask = { task -> navigator.toEditTask(task.id!!) }
        )
        binding.tasksAddButton.setDebounceClickListener { navigator.toNewTask() }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                TasksState.Loading -> showLoading()
                is TasksState.Error -> showError(state.message)
                is TasksState.Data -> {
                    if (state.tasks.isEmpty()) showEmptyTasks() else showData(state.tasks)
                }
            }
        }
    }

    private fun observeEvents() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                TasksEvent.AllTasksDone -> navigator.toConill()
                is TasksEvent.TaskDone -> showToast(resources.getString(R.string.tasks_done, event.taskName.uppercase()))
                is TasksEvent.ShowError -> showToast(event.message)
            }
        }
    }

    private fun showLoading() {
        binding.tasksProgress.progress.isVisible = true
    }

    private fun showError(message: Int) {
        binding.tasksProgress.progress.isVisible = false
        Snackbar.make(binding.root, message, LENGTH_INDEFINITE)
            .setAction(R.string.retry) { viewModel.retry() }
            .show()
    }

    private fun showEmptyTasks() {
        binding.tasksProgress.progress.isVisible = false
        binding.tasksEmptyText.isVisible = true
        binding.tasksAddButton.isVisible = true
    }

    private fun showData(tasks: List<Tasc>) {
        binding.tasksProgress.progress.isVisible = false
        binding.tasksEmptyText.isVisible = false
        binding.tasksAddButton.isVisible = true
        (binding.tasksRecyclerview.adapter as TasksAdapter).submitList(tasks)
    }
}