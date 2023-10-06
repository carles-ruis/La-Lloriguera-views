package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.TestScheduler
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test

class DeleteTaskTest {

    private val scheduler = TestScheduler()
    private val schedulers: AppSchedulers = AppSchedulers(scheduler, scheduler, scheduler)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: DeleteTask

    @Before
    fun setup() {
        usecase = DeleteTask(repository, schedulers)
    }

    @Test
    fun `given usecase, when task id is provided, then delete task through repository`() {
        every { repository.deleteTask(any()) } returns Completable.complete()
        usecase.execute(taskId).test().run {
            scheduler.triggerActions()
            assertComplete()
        }
        verify { repository.deleteTask(taskId) }
    }

    companion object {
        private const val taskId = "1"
    }

}