package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import com.carles.lallorigueraviews.model.Tasc
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class GetTaskTest {

    private val scheduler = TestScheduler()
    private val schedulers = AppSchedulers(scheduler, scheduler, scheduler)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: GetTask

    @Before
    fun setup() {
        usecase = GetTask(repository, schedulers)
    }

    @Test
    fun `given usecase, when id is provided, then get task from repository`() {
        coEvery { repository.getTask(any()) } returns Single.just(task)
        usecase.execute(taskId).test().run {
            scheduler.triggerActions()
            assertValue(task)
        }
        coVerify { repository.getTask(taskId) }
    }

    companion object {
        private const val taskId = "1"
        private val task = Tasc(taskId, "TASK!", true, 0L, 7, false)
    }
}