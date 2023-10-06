package com.carles.lallorigueraviews.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.data.remote.NoConnectionException
import com.carles.lallorigueraviews.domain.DeleteTask
import com.carles.lallorigueraviews.domain.GetTask
import com.carles.lallorigueraviews.domain.UpdateTask
import com.carles.lallorigueraviews.model.Tasc
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditTaskViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val getTask: GetTask = mockk()
    private val updateTask: UpdateTask = mockk()
    private val deleteTask: DeleteTask = mockk()
    private val savedState = SavedStateHandle().apply { set(EXTRA_TASK_ID, taskId) }
    private lateinit var viewModel: EditTaskViewModel

    @Before
    fun setup() {
        viewModel = EditTaskViewModel(getTask, updateTask, deleteTask, savedState)
    }

    @Test
    fun `given initialize, when get task is successful, then init task`() {
        every { getTask.execute(any()) } returns Single.just(task)
        viewModel.initialize()
        verify { getTask.execute(taskId) }
        assertNull(viewModel.event.value)
        assertEquals(TaskFormState.Filling(task), viewModel.state.value)
    }

    @Test
    fun `given initialize, when get task returns a no connection error, then show no connection error message`() {
        every { getTask.execute(any()) } returns Single.error(NoConnectionException())
        viewModel.initialize()
        verify { getTask.execute(taskId) }
        assertEquals(TaskFormEvent.ShowError(R.string.no_internet_connection, true), viewModel.event.value)
        assertEquals(TaskFormState.Loading, viewModel.state.value)
    }

    @Test
    fun `given initialize, when get task returns a different error, then show load error message`() {
        every { getTask.execute(any()) } returns Single.error(Exception("the exception of the rule"))
        viewModel.initialize()
        verify { getTask.execute(taskId) }
        assertEquals(TaskFormEvent.ShowError(R.string.edit_task_load_error, true), viewModel.event.value)
        assertEquals(TaskFormState.Loading, viewModel.state.value)
    }

    @Test
    fun `given onSaveClick, when update is successful, then send saved event`() {
        every { getTask.execute(any()) } returns Single.just(task)
        every { updateTask.execute(any()) } returns Completable.complete()
        viewModel.initialize()
        viewModel.onSaveClick()
        verify { getTask.execute(taskId) }
        verify { updateTask.execute(task) }
        assertEquals(TaskFormEvent.Saved(task.name), viewModel.event.value)
    }

    @Test
    fun `given onSaveClick, when update returns a connection error,then show error`() {
        every { getTask.execute(any()) } returns Single.just(task)
        every { updateTask.execute(any()) } returns Completable.error(NoConnectionException())
        viewModel.initialize()
        viewModel.onSaveClick()
        verify { getTask.execute(taskId) }
        verify { updateTask.execute(task) }
        assertEquals(TaskFormEvent.ShowError(R.string.no_internet_connection), viewModel.event.value)
    }

    @Test
    fun `given onSaveClick, when update returns a different error, then show save error`() {
        every { getTask.execute(any()) } returns Single.just(task)
        every { updateTask.execute(any()) } returns Completable.error(Exception("great exception"))
        viewModel.initialize()
        viewModel.onSaveClick()
        verify { getTask.execute(taskId) }
        verify { updateTask.execute(task) }
        assertEquals(TaskFormEvent.ShowError(R.string.edit_task_save_error), viewModel.event.value)
    }

    @Test
    fun `given onDeleteClick, when delete is successful, then delete the task`() {
        every { getTask.execute(any()) } returns Single.just(task)
        every { deleteTask.execute(any()) } returns Completable.complete()
        viewModel.initialize()
        viewModel.onDeleteClick()
        verify { getTask.execute(taskId) }
        verify { deleteTask.execute(taskId) }
        assertEquals(TaskFormEvent.Deleted(task.name), viewModel.event.value)
    }

    @Test
    fun `given onDeleteClick, when delete returns a connection error, then show no connection error message`() {
        every { getTask.execute(any()) } returns Single.just(task)
        every { deleteTask.execute(any()) } returns Completable.error(NoConnectionException())
        viewModel.initialize()
        viewModel.onDeleteClick()
        verify { getTask.execute(taskId) }
        verify { deleteTask.execute(taskId) }
        assertEquals(TaskFormEvent.ShowError(R.string.no_internet_connection), viewModel.event.value)
    }

    @Test
    fun `given onDeleteClick, when delete returns a different error, then show delete error message`() {
        every { getTask.execute(any()) } returns Single.just(task)
        every { deleteTask.execute(any()) } returns Completable.error(Exception("another error"))
        viewModel.initialize()
        viewModel.onDeleteClick()
        verify { getTask.execute(taskId) }
        verify { deleteTask.execute(taskId) }
        assertEquals(TaskFormEvent.ShowError(R.string.edit_task_delete_error), viewModel.event.value)
    }

    companion object {
        private const val EXTRA_TASK_ID = "extraTaskId"
        private const val taskId = "1"
        private val task = Tasc(taskId, "the task", false, 0L, 7, false)
    }

}