package com.carles.lallorigueraviews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.carles.lallorigueraviews.Navigator
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.databinding.FragmentEditTaskBinding
import com.carles.lallorigueraviews.databinding.ViewTaskFormBinding
import com.carles.lallorigueraviews.model.Tasc
import com.carles.lallorigueraviews.ui.common.setDebounceClickListener
import com.carles.lallorigueraviews.ui.common.showToast
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment.Companion.REQUEST_KEY_LAST_DATE
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment.Companion.REQUEST_KEY_NEXT_DATE
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment.Companion.RESULT_SELECTED_DAY
import com.carles.lallorigueraviews.ui.viewmodel.EditTaskViewModel
import com.carles.lallorigueraviews.ui.viewmodel.TaskFormEvent
import com.carles.lallorigueraviews.ui.viewmodel.TaskFormEvent2
import com.carles.lallorigueraviews.ui.viewmodel.TaskFormState
import com.carles.lallorigueraviews.ui.viewmodel.TaskFormState2
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EditTaskFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator

    private val viewModel: EditTaskViewModel by viewModels()
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!
    private val form by lazy {
        ViewTaskFormBinding.bind(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize()
        setFragmentResultListener(REQUEST_KEY_LAST_DATE) { _, bundle ->
            viewModel.onLastDateChange(bundle.getLong(RESULT_SELECTED_DAY))
        }
        setFragmentResultListener(REQUEST_KEY_NEXT_DATE) { _, bundle ->
            viewModel.onNextDateChange(bundle.getLong(RESULT_SELECTED_DAY))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initForm()
        initButtons()
        observeState()
        observeIsValid()
        observeEvents()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initForm() {
        form.taskFormNameEdittext.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChange(text.toString())
        }
        form.taskFormPeriodicButton.setOnClickListener { view ->
            if ((view as RadioButton).isChecked) viewModel.onPeriodicTaskCheck()
        }
        form.taskFormOneTimeButton.setOnClickListener { view ->
            if ((view as RadioButton).isChecked) viewModel.onOneTimeTaskCheck()
        }
        form.taskFormPeriodicityText.doOnTextChanged { text, _, _, _ ->
            val periodicity = text.toString().toInt()
            viewModel.onPeriodicityChange(periodicity)
            updatePeriodicDescriptionText(periodicity)
        }
        form.taskOneTimeDateText.setDebounceClickListener {
            viewModel.onOneTimeDateClick()
        }
        form.taskFormPeriodicDateText.setDebounceClickListener {
            viewModel.onPeriodicDateClick()
        }
    }

    private fun updatePeriodicDescriptionText(periodicity: Int) {
        form.taskFormPeriodicDescription.text =
            resources.getQuantityString(R.plurals.new_task_periodic_description, periodicity, periodicity)
    }

    private fun initButtons() {
        binding.editTaskSaveButton.setDebounceClickListener { viewModel.onSaveClick() }
        binding.editTaskDeleteButton.setDebounceClickListener { viewModel.onDeleteClick() }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskFormState2.Loading -> {
                    binding.editTaskContent.isVisible = false
                    binding.editTaskProgress.progress.isVisible = true
                }

                is TaskFormState2.Filling -> {
                    binding.editTaskContent.isVisible = true
                    binding.editTaskProgress.progress.isVisible = false
                    fillForm(state.task)
                }
            }
        }
    }

    private fun observeIsValid() {
        viewModel.isValid.observe(viewLifecycleOwner) { isValid ->
            form.taskFormNameInput.error = if (isValid) null else resources.getString(R.string.new_task_name_error)
            if (isValid.not()) form.taskFormNameInput.requestFocus()
        }
    }

    private fun observeEvents() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is TaskFormEvent2.ShowError -> {
                    showToast(event.message)
                    if (event.exit) navigator.up()
                }

                is TaskFormEvent2.Saved -> {
                    showToast(resources.getString(R.string.edit_task_saved, event.taskName.uppercase()))
                    navigator.up()
                }

                is TaskFormEvent2.NavigateToCalendar -> navigator.toCalendarDialog(
                    year = event.year,
                    month = event.month,
                    day = event.day,
                    minDate = event.minDate,
                    maxDate = event.maxDate,
                    requestKey = event.requestKey
                )

                is TaskFormEvent2.Deleted -> {
                    showToast(resources.getString(R.string.edit_task_deleted, event.taskName.uppercase()))
                    navigator.up()
                }
            }
        }
    }

    private fun fillForm(task: Tasc) {
        form.taskFormNameEdittext.setText(task.name)
        form.taskFormOneTimeButton.isChecked = task.isOneTime
        form.taskFormPeriodicButton.isChecked = task.isOneTime.not()

        form.taskFormPeriodicityText.setText(task.periodicity.toString())
        form.taskFormPeriodicityText.setSimpleItems(Array(30) { (it + 1).toString() })
        updatePeriodicDescriptionText(task.periodicity)

        form.taskOneTimeDateInput.isVisible = task.isOneTime
        form.taskOneTimeDateText.setText(formatter.format(task.nextDate))

        form.taskFormPeriodicityInput.isVisible = task.isOneTime.not()
        form.taskFormPeriodicDateInput.isVisible = task.isOneTime.not()
        form.taskFormPeriodicDateText.setText(formatter.format(task.lastDate))
    }
}

