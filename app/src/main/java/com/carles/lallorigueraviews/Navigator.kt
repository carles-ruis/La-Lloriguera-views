package com.carles.lallorigueraviews

import androidx.navigation.NavController
import com.carles.lallorigueraviews.ui.common.safeNavigate
import com.carles.lallorigueraviews.ui.fragments.TasksFragmentDirections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {

    private lateinit var controller: NavController

    fun initController(controller: NavController) {
        this.controller = controller
    }

    fun up() {
        controller.navigateUp()
    }

    fun toConill() {
        controller.safeNavigate { TasksFragmentDirections.toConill() }
    }

    fun toEditTask(taskId: String) {
        controller.safeNavigate { TasksFragmentDirections.toEditTask(taskId) }
    }

    fun toNewTask() {
        controller.safeNavigate { TasksFragmentDirections.toNewTask() }
    }

    fun toCalendarDialog(year: Int, month: Int, day: Int, minDate: Long, maxDate: Long, requestKey: String) {
        controller.safeNavigate { NavGraphDirections.toCalendarDialog(year, month, day, minDate, maxDate, requestKey) }
    }
}