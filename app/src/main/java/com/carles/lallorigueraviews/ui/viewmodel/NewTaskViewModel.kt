package com.carles.lallorigueraviews.ui.viewmodel

import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.common.TimeHelper
import com.carles.lallorigueraviews.domain.NewTask
import com.carles.lallorigueraviews.model.Tasc
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel @Inject constructor(private val newTask: NewTask) : TaskViewModel() {

    override fun initialize() {
        task = Tasc(
            id = null,
            name = "",
            isOneTime = false,
            lastDate = TimeHelper.now(),
            periodicity = DEFAULT_PERIODICITY,
            notificationsOn = false
        )
        updateForm()
    }

    fun onSaveClick() {
        onSaveClick(R.string.new_task_error) { newTask.execute(task) }
    }
}