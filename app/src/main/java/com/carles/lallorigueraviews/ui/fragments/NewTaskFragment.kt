package com.carles.lallorigueraviews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.carles.lallorigueraviews.databinding.FragmentNewTaskBinding
import com.carles.lallorigueraviews.databinding.ViewTaskFormBinding
import com.carles.lallorigueraviews.ui.common.setDebounceClickListener
import com.carles.lallorigueraviews.ui.viewmodel.NewTaskViewModel
import com.carles.lallorigueraviews.ui.viewmodel.TaskFormState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewTaskFragment : TaskFragment<NewTaskViewModel>() {

    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!

    override val viewModel: NewTaskViewModel by viewModels()
    override val form by lazy { ViewTaskFormBinding.bind(binding.root) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun initButtons() {
        binding.newTaskSaveButton.setDebounceClickListener { viewModel.onSaveClick() }
    }

    override fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskFormState.Loading -> {
                    binding.newTaskContent.isVisible = false
                    binding.newTaskProgress.progress.isVisible = true
                }

                is TaskFormState.Filling -> {
                    binding.newTaskContent.isVisible = true
                    binding.newTaskProgress.progress.isVisible = false
                    fillForm(state.task)
                }
            }
        }
    }
}
