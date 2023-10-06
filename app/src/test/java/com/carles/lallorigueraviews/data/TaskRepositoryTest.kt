package com.carles.lallorigueraviews.data

import com.carles.lallorigueraviews.model.Tasc
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {

    private val datasource: TaskDatasource = mockk()
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        repository = TaskRepository(datasource)
    }

    @Test
    fun `given getTask, when id is provided, then obtain task from datasource`() {
        every { datasource.getTask(any()) } returns Single.just(task)
        repository.getTask(taskId).test().assertValue(task)
        verify { datasource.getTask(taskId) }
    }

    @Test
    fun `given getTasks, when called, then obtain tasks from datasource`() {
        val tasks = listOf(task)
        every { datasource.getTasks() } returns Flowable.just(tasks)
        repository.getTasks().test().assertValue(tasks)
        verify { datasource.getTasks() }
    }

    @Test
    fun `given saveTask, when task is provided, then save it to datasource`() {
        every { datasource.saveTask(any()) } returns Completable.complete()
        repository.saveTask(task).test().assertComplete()
        verify { datasource.saveTask(task) }
    }

    @Test
    fun `given updateTask, when task is provided, then update it on datasource`() {
        every { datasource.updateTask(any()) } returns Completable.complete()
        repository.updateTask(task).test().assertComplete()
        verify { datasource.updateTask(task) }
    }

    @Test
    fun `given deleteTask, when id is provided, then delete task with given id`() {
        every { datasource.deleteTask(any()) } returns Completable.complete()
        repository.deleteTask(taskId).test().assertComplete()
        verify { datasource.deleteTask(taskId) }
    }

    companion object {
        private const val taskId = "1"
        private val task = Tasc(taskId, "TASK!", true, 0L, 7, false)
    }
}