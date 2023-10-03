package com.carles.lallorigueraviews.ui.viewmodel

import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.domain.NewTask
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel @Inject constructor(private val newTask: NewTask) : TaskViewModel() {

    override fun initialize() {
        task = NEW_TASK
        updateForm()
    }

    fun onSaveClick() {
        onSaveClick(R.string.new_task_error) { newTask.execute(task) }
    }
}