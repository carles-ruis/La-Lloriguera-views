package com.carles.lallorigueraviews.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.data.remote.NoConnectionException
import com.carles.lallorigueraviews.domain.GetTasks
import com.carles.lallorigueraviews.domain.MarkTaskAsDone
import com.carles.lallorigueraviews.model.Tasc
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test


class TasksViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val getTasks: GetTasks = mockk()
    private val markTaskAsDone: MarkTaskAsDone = mockk()
    private val delegate: HasPendingTasksDelegate = mockk()
    private lateinit var viewModel: TasksViewModel

    private fun initViewModel() {
        every { getTasks.execute() } returns Flowable.just(tasks)
        viewModel = TasksViewModel(getTasks, markTaskAsDone, delegate)
    }

    @Test
    fun `given initialization, when tasks are obtained with success, then set tasks data state`() {
        initViewModel()
        verify { getTasks.execute() }
        assertTrue(viewModel.state.value == TasksState.Data(tasks))
    }

    @Test
    fun `given initialization, when there is a no connection exception, then set error state with no connection message`() {
        every { getTasks.execute() } returns Flowable.error(NoConnectionException())
        viewModel = TasksViewModel(getTasks, markTaskAsDone, delegate)
        verify { getTasks.execute() }
        assertTrue(viewModel.state.value == TasksState.Error(R.string.no_internet_connection))
    }

    @Test
    fun `given retry, when called, then get tasks`() {
        initViewModel()
        viewModel.retry()
        verify(exactly = 2) { getTasks.execute() }
    }

    @Test
    fun `given onTaskDone, when is marked successfully, then send task done event`() {
        initViewModel()
        every { markTaskAsDone.execute(any()) } returns Completable.complete()
        viewModel.onTaskDone(task1)
        verify { markTaskAsDone.execute(task1) }
        assertTrue(viewModel.event.value == TasksEvent.TaskDone(task1.name))
    }

    @Test
    fun `given onTaskDone, when there is a connection error, then show connection error message`() {
        initViewModel()
        every { markTaskAsDone.execute(any()) } returns Completable.error(NoConnectionException())
        viewModel.onTaskDone(task1)
        verify { markTaskAsDone.execute(task1) }
        assertTrue(viewModel.event.value == TasksEvent.ShowError(R.string.no_internet_connection))
    }

    @Test
    fun `given onTaskDone, when there is a different error, then show mark as done error message`() {
        initViewModel()
        every { markTaskAsDone.execute(any()) } returns Completable.error(Exception("an exception for you"))
        viewModel.onTaskDone(task1)
        verify { markTaskAsDone.execute(task1) }
        assertTrue(viewModel.event.value == TasksEvent.ShowError(R.string.tasks_mark_as_done_error))
    }

    @Test
    fun `given checkIfHasNoPendingTasks, when previous state is loading, then do nothing`() {
        initViewModel()
        verify(exactly = 0) { delegate.hasPendingTasks(any()) }
    }

    @Test
    fun `given checkIfHasNoPendingTasks, when previous state is data and had no pending tasks, then do nothing`() {
        initViewModel()
        every { delegate.hasPendingTasks(any()) } returnsMany (listOf(false, false))
        viewModel.checkIfHasNoPendingTasks(tasks)
        verify(exactly = 2) { delegate.hasPendingTasks(tasks) }
    }

    @Test
    fun `given checkIfHasNoPendingTasks, when previous state is data and had and has still pending tasks, then do nothing`() {
        initViewModel()
        every { delegate.hasPendingTasks(any()) } returnsMany (listOf(true, true))
        viewModel.checkIfHasNoPendingTasks(tasks)
        verify(exactly = 2) { delegate.hasPendingTasks(tasks) }
    }

    @Test
    fun `given checkIfHasNoPendingTasks, when had pending tasks and has no pending tasks, then send AllTasksDone event`() {
            initViewModel()
            every { delegate.hasPendingTasks(any()) } returnsMany (listOf(true, false))
            viewModel.checkIfHasNoPendingTasks(tasks)
            verify(exactly = 2) { delegate.hasPendingTasks(tasks) }
            assertTrue(viewModel.event.value == TasksEvent.AllTasksDone)
        }

    companion object {
        private val task1 = Tasc("1", "task 1", false, 0L, 7, false)
        private val task2 = Tasc("2", "task 2", true, 0L, 7, false)
        private val tasks = listOf(task1, task2)
    }
}