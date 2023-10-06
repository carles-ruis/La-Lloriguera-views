package com.carles.lallorigueraviews.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carles.lallorigueraviews.common.TimeHelper
import com.carles.lallorigueraviews.model.Tasc
import com.carles.lallorigueraviews.ui.fragments.CalendarDialogFragment
import io.mockk.every
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: TaskViewModelImpl

    inner class TaskViewModelImpl : TaskViewModel() {
        override fun initialize() {
            this.task = superTask
            updateForm()
        }

        val currentTask: Tasc
            get() = task
    }

    @Before
    fun setup() {
        viewModel = TaskViewModelImpl()
    }

    @Test
    fun `given onNameChange, when name is passed, then update task with this name and set isValid to true`() {
        val newName = "new task name"
        val expected = superTask.copy(name = newName)
        viewModel.initialize()
        viewModel.onNameChange(newName)

        assertEquals(expected, viewModel.currentTask)
        assertTrue(viewModel.isValid.value!!)
    }

    @Test
    fun `given onOneTimeTaskCheck, when called, then update form with the expected values`() {
        val last = 1_000L
        val expected = superTask.copy(isOneTime = true, lastDate = last, periodicity = DEFAULT_PERIODICITY)
        mockkObject(TimeHelper)
        every { TimeHelper.now() } returns last

        viewModel.initialize()
        viewModel.onPeriodicityChange(5)
        viewModel.onOneTimeTaskCheck()
        assertEquals(TaskFormState.Filling(expected), viewModel.state.value)
    }

    @Test
    fun `given onPeriodicTaskCheck, when called, then update form with the expected values`() {
        val last = 1_000L
        val expected = superTask.copy(isOneTime = false, lastDate = last, periodicity = DEFAULT_PERIODICITY)
        mockkObject(TimeHelper)
        every { TimeHelper.now() } returns last

        viewModel.initialize()
        viewModel.onPeriodicityChange(5)
        viewModel.onPeriodicTaskCheck()
        assertEquals(TaskFormState.Filling(expected), viewModel.state.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given onLastDateChange, when task is one time, then throw exception`() {
        viewModel.initialize()
        viewModel.onOneTimeTaskCheck()
        viewModel.onLastDateChange(1_000L)
    }

    @Test
    fun `given onLastDateChange, when lastDate is passed, then update form with last date`() {
        val lastDate = 1_000L
        val expected = superTask.copy(lastDate = lastDate)
        viewModel.initialize()
        viewModel.onLastDateChange(lastDate)
        assertEquals(TaskFormState.Filling(expected), viewModel.state.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given onNextDateChange, when task is periodic, then throw exception`() {
        viewModel.initialize()
        viewModel.onPeriodicTaskCheck()
        viewModel.onNextDateChange(1_000L)
    }

    @Test
    fun `given onNextDateChange, when nextDate is passed, then update form with the expected values`() {
        val now = 1696362556743L // 3-10-2023
        val next = 1696579200000 // 6-10-2023
        val periodicity = 3
        val expected = superTask.copy(isOneTime = true, lastDate = now, periodicity = periodicity)
        mockkObject(TimeHelper)
        every { TimeHelper.now() } returns now

        viewModel.initialize()
        viewModel.onOneTimeTaskCheck()
        viewModel.onNextDateChange(next)

        assertEquals(TaskFormState.Filling(expected), viewModel.state.value)
    }

    @Test
    fun `given onPeriodicityChange, when periodicity is passed, then update task value`() {
        val periodicity = 9
        viewModel.initialize()
        viewModel.onPeriodicityChange(periodicity)
        assertEquals(periodicity, viewModel.currentTask.periodicity)
    }

    @Test
    fun `given onOneTimeDateClick, when called, then navigate to calendar`() {
        val now = 1696362556743L // 3-10-2023
        val next = 1696579200000L // 6-10-2023
        mockkObject(TimeHelper)
        every { TimeHelper.now() } returns now

        viewModel.initialize()
        viewModel.onOneTimeTaskCheck()
        viewModel.onNextDateChange(next)
        viewModel.onOneTimeDateClick()

        val event = TaskFormEvent.NavigateToCalendar(
            2023, 10, 6, now, now + 30 * TimeHelper.DAYS_TO_MILLIS, CalendarDialogFragment.REQUEST_KEY_NEXT_DATE
        )
        assertEquals(event, viewModel.event.value)
    }

    @Test
    fun `given onPeriodicDateClick, when called, then navigate to calendar`() {
        val last = 1696147200000L // 1-10-2023
        val now = 1696362556743L // 3-10-2023
        mockkObject(TimeHelper)
        every { TimeHelper.now() } returns now

        viewModel.initialize()
        viewModel.onPeriodicDateClick()
        viewModel.onLastDateChange(last)
        viewModel.onPeriodicDateClick()

        val event = TaskFormEvent.NavigateToCalendar(
            2023, 10, 1, now - 30 * TimeHelper.DAYS_TO_MILLIS, now, CalendarDialogFragment.REQUEST_KEY_LAST_DATE
        )
        assertEquals(event, viewModel.event.value)
    }

    companion object {
        private const val DEFAULT_PERIODICITY = 7

        private val superTask = Tasc(
            id = "1",
            name = "super task",
            isOneTime = false,
            lastDate = 0L,
            periodicity = 7,
            notificationsOn = false
        )
    }
}