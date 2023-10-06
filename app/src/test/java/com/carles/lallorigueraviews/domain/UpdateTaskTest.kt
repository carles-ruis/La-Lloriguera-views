package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import com.carles.lallorigueraviews.model.Tasc
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class UpdateTaskTest {


    private val scheduler = TestScheduler()
    private val schedulers = AppSchedulers(scheduler, scheduler, scheduler)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: UpdateTask

    @Before
    fun setup() {
        usecase = UpdateTask(repository, schedulers)
    }


    @Test
    fun `given usecase, when task is provided, then update task in the repository`() {
        every { repository.updateTask(any()) } returns Completable.complete()
        usecase.execute(task).test().run {
            scheduler.triggerActions()
            assertComplete()
        }
        verify { repository.updateTask(task)}
    }

    companion object {
        private val task = Tasc("1", "TASK!", true, 0L, 7, false)
    }
}