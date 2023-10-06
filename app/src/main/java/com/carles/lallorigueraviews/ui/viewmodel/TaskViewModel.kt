package com.carles.lallorigueraviews.ui.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.common.TimeHelper
import com.carles.lallorigueraviews.data.remote.NoConnectionException
import com.carles.lallorigueraviews.model.Tasc
import com.carles.lallorigueraviews.ui.common.LiveEvent
import com.carles.lallorigueraviews.ui.common.addTo
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime

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

    data class Deleted(val taskName: String) : TaskFormEvent()
}

sealed class TaskFormState {
    data object Loading : TaskFormState()
    data class Filling(val task: Tasc) : TaskFormState()
}

abstract class TaskViewModel : ViewModel() {

    abstract fun initialize()

    protected val disposables = CompositeDisposable()

    protected lateinit var task: Tasc
    protected val isTaskInitialized: Boolean
        get() = this::task.isInitialized

    protected var _state = MutableLiveData<TaskFormState>()
    val state: LiveData<TaskFormState> = _state

    protected var _event = LiveEvent<TaskFormEvent>()
    val event: LiveData<TaskFormEvent> = _event

    private var _isValid = MutableLiveData(true)
    val isValid: LiveData<Boolean> = _isValid

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    protected fun updateForm() {
        _state.value = TaskFormState.Filling(task)
    }

    fun onNameChange(name: String) {
        task = task.copy(name = name)
        if (name.isNotEmpty()) _isValid.value = true
    }

    fun onOneTimeTaskCheck() = onOneTimeTaskChange(true)
    fun onPeriodicTaskCheck() = onOneTimeTaskChange(false)

    private fun onOneTimeTaskChange(isOneTime: Boolean) {
        task = task.copy(
            isOneTime = isOneTime,
            lastDate = TimeHelper.now(),
            periodicity = DEFAULT_PERIODICITY
        )
        updateForm()
    }

    fun onLastDateChange(lastDate: Long) {
        check(task.isOneTime.not()) { "Cannot change last date on a one time task" }
        task = task.copy(lastDate = lastDate)
        updateForm()
    }

    fun onNextDateChange(nextDate: Long) {
        check(task.isOneTime) { "Cannot change next date on a periodic task" }
        val now = TimeHelper.now()
        val daysRemaining = TimeHelper.getDaysBetweenDates(now, nextDate)
        task = task.copy(lastDate = now, periodicity = daysRemaining)
        updateForm()
    }

    fun onPeriodicityChange(periodicity: Int) {
        task = task.copy(periodicity = periodicity)
    }

    fun onOneTimeDateClick() {
        val nextDate = DateTime(task.nextDate)
        val now = TimeHelper.now()
        _event.value = TaskFormEvent.NavigateToCalendar(
            year = nextDate.year,
            month = nextDate.monthOfYear,
            day = nextDate.dayOfMonth,
            minDate = now,
            maxDate = now + 30 * TimeHelper.DAYS_TO_MILLIS,
            requestKey = CalendarDialogFragment.REQUEST_KEY_NEXT_DATE
        )
    }

    fun onPeriodicDateClick() {
        val lastDate = DateTime(task.lastDate)
        val now = TimeHelper.now()
        _event.value = TaskFormEvent.NavigateToCalendar(
            year = lastDate.year,
            month = lastDate.monthOfYear,
            day = lastDate.dayOfMonth,
            minDate = now - 30 * TimeHelper.DAYS_TO_MILLIS,
            maxDate = now,
            requestKey = CalendarDialogFragment.REQUEST_KEY_LAST_DATE
        )
    }

    protected fun onSaveClick(@StringRes errorMessage: Int, action: (Tasc) -> Completable) {
        if (task.name.isEmpty()) {
            _isValid.value = false
        } else {
            _state.value = TaskFormState.Loading
            action.invoke(task).subscribe({
                _event.value = TaskFormEvent.Saved(task.name)
            }, { exception ->
                Log.w("TaskViewModel", exception.localizedMessage ?: "onSaveClick error")
                _state.value = TaskFormState.Filling(task)
                _event.value =
                    TaskFormEvent.ShowError(if (exception is NoConnectionException) R.string.no_internet_connection else errorMessage)
            }).addTo(disposables)
        }

    }

    companion object {
        @JvmStatic
        protected val DEFAULT_PERIODICITY = 7
    }
}