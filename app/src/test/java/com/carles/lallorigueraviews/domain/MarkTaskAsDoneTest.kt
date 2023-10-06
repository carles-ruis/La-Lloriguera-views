package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import com.carles.lallorigueraviews.model.Tasc
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.schedulers.TestScheduler
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class MarkTaskAsDoneTest {

    private val scheduler = TestScheduler()
    private val schedulers = AppSchedulers(scheduler, scheduler, scheduler)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: MarkTaskAsDone

    @Before
    fun setup() {
        usecase = MarkTaskAsDone(repository, schedulers)
    }

    @Test
    fun `given usecase, when task is one time, then delete the task`() {
        every { repository.deleteTask(any()) } returns Completable.complete()
        usecase.execute(oneTimeTask).test().run {
            scheduler.triggerActions()
            assertComplete()
        }
        verify { repository.deleteTask(oneTimeTask.id!!) }
    }

    @Test
    fun `given usecase is executed, when task is periodic, then update its last date`() {
        every { repository.updateTask(any()) } returns Completable.complete()
        usecase.execute(periodicTask).test().run {
            scheduler.triggerActions()
            assertComplete()
        }

        val slot = slot<Tasc>()
        verify { repository.updateTask(capture(slot)) }
        val updatedTask = slot.captured
        assertEquals(updatedTask.id, periodicTask.id)
        assertEquals(updatedTask.name, periodicTask.name)
        assertEquals(updatedTask.periodicity, periodicTask.periodicity)
        assertEquals(updatedTask.isOneTime, periodicTask.isOneTime)
    }

    companion object {
        private val oneTimeTask = Tasc("1", "TASK!", true, 0L, 7, false)
        private val periodicTask = Tasc("2", "TASK!!!", false, 0L, 7, false)
    }

}