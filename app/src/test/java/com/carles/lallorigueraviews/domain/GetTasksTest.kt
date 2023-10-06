package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import com.carles.lallorigueraviews.model.Tasc
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class GetTasksTest {

    private val scheduler = TestScheduler()
    private val schedulers = AppSchedulers(scheduler, scheduler, scheduler)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: GetTasks

    @Before
    fun setup() {
        usecase = GetTasks(repository, schedulers)
    }

    @Test
    fun `given usecase, when executed, then get tasks from repository`() {
        val tasks = listOf(task)
        every { repository.getTasks() } returns Flowable.just(tasks)
        usecase.execute().test().run {
            scheduler.triggerActions()
            assertValue(tasks)
        }
        verify { repository.getTasks() }
    }

    companion object {
        private val task = Tasc("1", "TASK!", true, 0L, 7, false)
    }
}