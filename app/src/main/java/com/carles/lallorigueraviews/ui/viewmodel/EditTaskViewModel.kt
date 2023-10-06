package com.carles.lallorigueraviews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.data.remote.NoConnectionException
import com.carles.lallorigueraviews.domain.DeleteTask
import com.carles.lallorigueraviews.domain.GetTask
import com.carles.lallorigueraviews.domain.UpdateTask
import com.carles.lallorigueraviews.ui.common.addTo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val getTask: GetTask,
    private val updateTask: UpdateTask,
    private val deleteTask: DeleteTask,
    savedStateHandle: SavedStateHandle
) : TaskViewModel() {

    private val taskId = savedStateHandle.get<String>(EXTRA_TASK_ID) ?: "0"

    override fun initialize() {
        if (isTaskInitialized) {
            updateForm()
        } else {
            getTask()
        }
    }

    private fun getTask() {
        _state.value = TaskFormState.Loading
        getTask.execute(taskId).subscribe({ task ->
            this.task = task
            updateForm()
        }, { exception ->
            Log.w("EditTaskViewModel", exception.localizedMessage ?: "getTask error")
            _event.value = TaskFormEvent.ShowError(
                message = if (exception is NoConnectionException) R.string.no_internet_connection else R.string.edit_task_load_error,
                exit = true
            )
        }).addTo(disposables)
    }

    fun onSaveClick() =
        onSaveClick(R.string.edit_task_save_error) { updateTask.execute(task) }

    fun onDeleteClick() {
        deleteTask.execute(taskId).subscribe({
            _event.value = TaskFormEvent.Deleted(task.name)
        }, { exception ->
            Log.w("EditTaskViewModel", exception.localizedMessage ?: "onDeleteClick error")
            _event.value = TaskFormEvent.ShowError(
                if (exception is NoConnectionException) R.string.no_internet_connection else R.string.edit_task_delete_error
            )
        }).addTo(disposables)
    }

    companion object {
        private const val EXTRA_TASK_ID = "extraTaskId"

    }
}