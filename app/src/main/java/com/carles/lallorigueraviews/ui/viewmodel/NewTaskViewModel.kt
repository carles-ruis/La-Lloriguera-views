package com.carles.lallorigueraviews.ui.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.common.TimeHelper
import com.carles.lallorigueraviews.common.TimeHelper.Companion.DAYS_TO_MILLIS
import com.carles.lallorigueraviews.domain.NewTask
import com.carles.lallorigueraviews.model.Tasc
import com.carles.lallorigueraviews.ui.common.LiveEvent
import com.carles.lallorigueraviews.ui.common.addTo
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment.Companion.REQUEST_KEY_LAST_DATE
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment.Companion.REQUEST_KEY_NEXT_DATE
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime
import javax.inject.Inject

sealed class TaskFormEvent {
    data class ShowError(@StringRes val message: Int, val exit: Boolean = false) : TaskFormEvent()

    data class Saved(val taskName: String) : TaskFormEvent()

    data class NavigateToCalendar(
        val year: Int,
        val month: Int,
        val day: Int,
        val minDate: Long,
        val maxDate: Long,
        val requestKey: String
    ) : TaskFormEvent()
}

sealed class TaskFormState {
    data object Loading : TaskFormState()
    data class Filling(val task: Tasc) : TaskFormState()
}

@HiltViewModel
class NewTaskViewModel @Inject constructor(private val newTask: NewTask) : ViewModel() {

    private val disposables = CompositeDisposable()

    private var task = NEW_TASK

    private var _state = MutableLiveData<TaskFormState>(TaskFormState.Filling(task))
    val state: LiveData<TaskFormState> = _state

    private var _event = LiveEvent<TaskFormEvent>()
    val event: LiveData<TaskFormEvent> = _event

    private var _isValid = MutableLiveData(true)
    val isValid: LiveData<Boolean> = _isValid

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun initialize() {
        updateForm()
    }

    private fun updateForm() {
        _state.value = TaskFormState.Filling(task)
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
        _event.value = TaskFormEvent.NavigateToCalendar(
            year = nextDate.year,
            month = nextDate.monthOfYear,
            day = nextDate.dayOfMonth,
            minDate = now,
            maxDate = now + 30 * DAYS_TO_MILLIS,
            requestKey = REQUEST_KEY_NEXT_DATE
        )
    }

    fun onPeriodicDateClick() {
        val lastDate = DateTime(task.lastDate)
        val now = System.currentTimeMillis()
        _event.value = TaskFormEvent.NavigateToCalendar(
            year = lastDate.year,
            month = lastDate.monthOfYear,
            day = lastDate.dayOfMonth,
            minDate = now - 30 * DAYS_TO_MILLIS,
            maxDate = now,
            requestKey = REQUEST_KEY_LAST_DATE
        )
    }

    fun onSaveClick() {
        if (task.name.isEmpty()) {
            _isValid.value = false
        } else {
            _state.value = TaskFormState.Loading
            newTask.execute(task).subscribe({
                _event.value = TaskFormEvent.Saved(task.name)
            }, { exception ->
                Log.w("TaskFormDelegate", exception.localizedMessage ?: "onSaveClick error")
                _state.value = TaskFormState.Filling(task)
                _event.value = TaskFormEvent.ShowError(R.string.new_task_error)
            }).addTo(disposables)
        }
    }

    companion object {
        private const val DEFAULT_PERIODICITY = 7

        private val NEW_TASK = Tasc(
            id = null,
            name = "",
            isOneTime = false,
            lastDate = System.currentTimeMillis(),
            periodicity = DEFAULT_PERIODICITY,
            notificationsOn = false
        )
    }

}