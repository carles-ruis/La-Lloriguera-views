package com.carles.lallorigueraviews.ui.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.common.TimeHelper
import com.carles.lallorigueraviews.domain.DeleteTask
import com.carles.lallorigueraviews.domain.GetTask
import com.carles.lallorigueraviews.domain.UpdateTask
import com.carles.lallorigueraviews.model.Tasc
import com.carles.lallorigueraviews.ui.common.LiveEvent
import com.carles.lallorigueraviews.ui.common.addTo
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment.Companion.REQUEST_KEY_LAST_DATE
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment.Companion.REQUEST_KEY_NEXT_DATE
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime
import javax.inject.Inject

sealed class TaskFormEvent2 {
    data class ShowError(@StringRes val message: Int, val exit: Boolean = false) : TaskFormEvent2()

    data class Saved(val taskName: String) : TaskFormEvent2()

    data class NavigateToCalendar(
        val year: Int,
        val month: Int,
        val day: Int,
        val minDate: Long,
        val maxDate: Long,
        val requestKey: String
    ) : TaskFormEvent2()

    data class Deleted(val taskName: String) : TaskFormEvent2()
}

sealed class TaskFormState2 {
    data object Loading : TaskFormState2()
    data class Filling(val task: Tasc) : TaskFormState2()
}

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val getTask: GetTask,
    private val updateTask: UpdateTask,
    private val deleteTask: DeleteTask,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId = savedStateHandle.get<String>(EXTRA_TASK_ID) ?: "0"
    private val disposables = CompositeDisposable()

    private lateinit var task: Tasc

    private var _state = MutableLiveData<TaskFormState2>(TaskFormState2.Loading)
    val state: LiveData<TaskFormState2> = _state

    private var _event = LiveEvent<TaskFormEvent2>()
    val event: LiveData<TaskFormEvent2> = _event

    private var _isValid = MutableLiveData(true)
    val isValid: LiveData<Boolean> = _isValid

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun initialize() {
        if (this::task.isInitialized) {
            updateForm()
        } else {
            getTask()
        }
    }

    private fun updateForm() {
        _state.value = TaskFormState2.Filling(task)
    }

    fun onNameChange(name: String) {
        task = task.copy(name = name)
        if (name.isNotEmpty()) _isValid.value = true
    }

    fun onOneTimeTaskCheck() = onOneTimeTaskChange(true)
    fun onPeriodicTaskCheck() = onOneTimeTaskChange(false)

    private fun onOneTimeTaskChange(isOneTime: Boolean) {
        task = task.copy(isOneTime = isOneTime, lastDate = System.currentTimeMillis(), periodicity = DEFAULT_PERIODICITY)
        updateForm()
    }

    fun onLastDateChange(lastDate: Long) {
        require(task.isOneTime.not()) { "Cannot change last date on a one time task" }
        task = task.copy(lastDate = lastDate)
        updateForm()
    }

    fun onNextDateChange(nextDate: Long) {
        require(task.isOneTime) { "Cannot change next date on a periodic task" }
        val now = System.currentTimeMillis()
        val daysRemaining = TimeHelper.getDaysBetweenDates(now, nextDate)
        task = task.copy(lastDate = now, periodicity = daysRemaining)
        updateForm()
    }

    fun onPeriodicityChange(periodicity: Int) {
        task = task.copy(periodicity = periodicity)
    }

    fun onOneTimeDateClick() {
        val nextDate = DateTime(task.nextDate)
        val now = System.currentTimeMillis()
        _event.value = TaskFormEvent2.NavigateToCalendar(
            year = nextDate.year,
            month = nextDate.monthOfYear,
            day = nextDate.dayOfMonth,
            minDate = now,
            maxDate = now + 30 * TimeHelper.DAYS_TO_MILLIS,
            requestKey = REQUEST_KEY_NEXT_DATE
        )
    }

    fun onPeriodicDateClick() {
        val lastDate = DateTime(task.lastDate)
        val now = System.currentTimeMillis()
        _event.value = TaskFormEvent2.NavigateToCalendar(
            year = lastDate.year,
            month = lastDate.monthOfYear,
            day = lastDate.dayOfMonth,
            minDate = now - 30 * TimeHelper.DAYS_TO_MILLIS,
            maxDate = now,
            requestKey = REQUEST_KEY_LAST_DATE
        )
    }

    private fun getTask() {
        _state.value = TaskFormState2.Loading
        getTask.execute(taskId).subscribe({ task ->
            this.task = task
            updateForm()
        }, { exception ->
            Log.w("EditTaskViewModel", exception.localizedMessage ?: "getTask error")
            _event.value = TaskFormEvent2.ShowError(R.string.edit_task_load_error, exit = true)
        }).addTo(disposables)
    }

    fun onSaveClick() {
        if (task.name.isEmpty()) {
            _isValid.value = false
        } else {
            _state.value = TaskFormState2.Loading
            updateTask.execute(task).subscribe({
                _event.value = TaskFormEvent2.Saved(task.name)
            }, { exception ->
                Log.w("TaskFormDelegate", exception.localizedMessage ?: "onSaveClick error")
                _state.value = TaskFormState2.Filling(task)
                _event.value = TaskFormEvent2.ShowError(R.string.edit_task_save_error)
            }).addTo(disposables)
        }
    }

    fun onDeleteClick() {
        deleteTask.execute(taskId).subscribe({
            _event.value = TaskFormEvent2.Deleted(task.name)
        }, { exception ->
            Log.w("EditTaskViewModel", exception.localizedMessage ?: "onDeleteClick error")
            _event.value = TaskFormEvent2.ShowError(R.string.edit_task_delete_error)
        }).addTo(disposables)
    }

    companion object {
        private const val EXTRA_TASK_ID = "extraTaskId"

        private const val DEFAULT_PERIODICITY = 7
    }
}