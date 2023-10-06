package com.carles.lallorigueraviews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.databinding.FragmentEditTaskBinding
import com.carles.lallorigueraviews.databinding.ViewTaskFormBinding
import com.carles.lallorigueraviews.ui.common.setDebounceClickListener
import com.carles.lallorigueraviews.ui.viewmodel.EditTaskViewModel
import com.carles.lallorigueraviews.ui.viewmodel.TaskFormState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskFragment : TaskFragment<EditTaskViewModel>() {

    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    override val viewModel: EditTaskViewModel by viewModels()
    override val form by lazy { ViewTaskFormBinding.bind(binding.root) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskFormState.Loading -> {
                    binding.editTaskContent.isVisible = false
                    binding.editTaskProgress.progress.isVisible = true
                }

                is TaskFormState.Filling -> {
                    binding.editTaskContent.isVisible = true
                    binding.editTaskProgress.progress.isVisible = false
                    fillForm(state.task)
                }
            }
        }
    }

    override fun initButtons() {
        binding.editTaskSaveButton.setDebounceClickListener { viewModel.onSaveClick() }
        binding.editTaskDeleteButton.setDebounceClickListener { showDeleteDialog() }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_task_delete)
            .setMessage(R.string.edit_task_delete_confirmation)
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.onDeleteClick()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
