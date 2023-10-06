package com.carles.lallorigueraviews.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.common.TimeHelper
import com.carles.lallorigueraviews.data.remote.NoConnectionException
import com.carles.lallorigueraviews.domain.NewTask
import com.carles.lallorigueraviews.model.Tasc
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import io.reactivex.Completable
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewTaskViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val newTask: NewTask = mockk()
    private lateinit var viewModel: NewTaskViewModel

    @Before
    fun setup() {
        viewModel = NewTaskViewModel(newTask)
        mockkObject(TimeHelper)
        every { TimeHelper.now() } returns 777L
    }

    @Test
    fun `given initialized, when called, then init default task`() {
        viewModel.initialize()
        assertEquals(TaskFormState.Filling(NEW_TASK), viewModel.state.value)
    }

    @Test
    fun `given onSaveClick, when task name is empty, then isValid is false`() {
        viewModel.initialize()
        viewModel.onSaveClick()
        assertNull(viewModel.event.value)
        assertFalse(viewModel.isValid.value!!)
    }

    @Test
    fun `given onSaveClick, when task is saved, then set event to saved`() {
        val taskName = "the task name"
        val task = NEW_TASK.copy(name = taskName)
        every { newTask.execute(any()) } returns Completable.complete()

        viewModel.initialize()
        viewModel.onNameChange(taskName)
        viewModel.onSaveClick()
        verify { newTask.execute(task) }
        assertEquals(TaskFormEvent.Saved(taskName), viewModel.event.value)
        assertTrue(viewModel.isValid.value!!)
    }

    @Test
    fun `given onSaveClick, when save returns a no connection error, then show no connection error`() {
        val taskName = "the task name"
        val task = NEW_TASK.copy(name = taskName)
        every { newTask.execute(any()) } returns Completable.error(NoConnectionException())

        viewModel.initialize()
        viewModel.onNameChange(taskName)
        viewModel.onSaveClick()
        verify { newTask.execute(task) }
        assertEquals(TaskFormState.Filling(task), viewModel.state.value)
        assertEquals(TaskFormEvent.ShowError(R.string.no_internet_connection), viewModel.event.value)
    }

    @Test
    fun `given onSaveClick, when save returns a different error, then show save error`() {
        val taskName = "the task name"
        val task = NEW_TASK.copy(name = taskName)
        every { newTask.execute(any()) } returns Completable.error(Exception("dammit"))

        viewModel.initialize()
        viewModel.onNameChange(taskName)
        viewModel.onSaveClick()
        verify { newTask.execute(task) }
        assertEquals(TaskFormState.Filling(task), viewModel.state.value)
        assertEquals(TaskFormEvent.ShowError(R.string.new_task_error), viewModel.event.value)
    }

    companion object {
        private const val DEFAULT_PERIODICITY = 7

        private val NEW_TASK = Tasc(
            id = null,
            name = "",
            isOneTime = false,
            lastDate = 777L,
            periodicity = DEFAULT_PERIODICITY,
            notificationsOn = false
        )
    }
}