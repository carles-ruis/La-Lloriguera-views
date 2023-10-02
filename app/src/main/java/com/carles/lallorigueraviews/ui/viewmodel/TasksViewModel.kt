package com.carles.lallorigueraviews.ui.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.domain.GetTasks
import com.carles.lallorigueraviews.domain.MarkTaskAsDone
import com.carles.lallorigueraviews.model.Tasc
import com.carles.lallorigueraviews.ui.common.LiveEvent
import com.carles.lallorigueraviews.ui.common.addTo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

sealed class TasksState {
    data object Loading : TasksState()
    data class Error(@StringRes val message: Int) : TasksState()
    data class Data(val tasks: List<Tasc>) : TasksState()
}

sealed class TasksEvent {
    data object AllTasksDone : TasksEvent()
    data class TaskDone(val taskName: String) : TasksEvent()
    data class ShowError(@StringRes val message: Int) : TasksEvent()
}

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasks: GetTasks,
    private val markTaskAsDone: MarkTaskAsDone,
    private val hasPendingTasksDelegate: HasPendingTasksDelegate
) : ViewModel(), HasPendingTasks by hasPendingTasksDelegate {

    private var _state = MutableLiveData<TasksState>(TasksState.Loading)
    val state: LiveData<TasksState> = _state

    private var _event = LiveEvent<TasksEvent>()
    val event: LiveData<TasksEvent> = _event

    private val disposables = CompositeDisposable()

    init {
        getTasks()
    }

    private fun getTasks() {
        _state.value = TasksState.Loading
        getTasks.execute().subscribe({ tasks ->
            checkIfHasNoPendingTasks(tasks)
            _state.value = TasksState.Data(tasks)
        }, { exception ->
            Log.w("TasksViewModel", exception.localizedMessage ?: "getTasks error")
            _state.value = TasksState.Error(R.string.tasks_error)
        }).addTo(disposables)
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun retry() {
        getTasks()
    }

    fun onTaskDone(task: Tasc) {
        markTaskAsDone.execute(task).subscribe({
            _event.value = TasksEvent.TaskDone(task.name)
        }, { exception ->
            Log.w("TasksViewModel", exception.localizedMessage ?: "onTaskDone error")
            _event.value = TasksEvent.ShowError(R.string.tasks_mark_as_done_error)
        }).addTo(disposables)
    }

    @VisibleForTesting
    fun checkIfHasNoPendingTasks(tasks: List<Tasc>) {
        // if state is a TasksState.Data it is not first load , so user has updated or done a task
        state.value.run {
            if (this is TasksState.Data) {
                val hadPendingTasks = hasPendingTasks(this.tasks)
                val hasPendingTasks = hasPendingTasks(tasks)
                if (hadPendingTasks and !hasPendingTasks) {
                    _event.value = TasksEvent.AllTasksDone
                }
            }
        }
    }
}