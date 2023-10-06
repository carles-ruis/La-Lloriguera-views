package com.carles.lallorigueraviews.data.local

import com.carles.lallorigueraviews.data.TaskMapper
import com.carles.lallorigueraviews.model.Tasc
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class TaskLocalDatasourceTest {


    private val mapper: TaskMapper = mockk()
    private val dao: TaskDao = mockk()
    private lateinit var datasource: TaskLocalDatasource

    @Before
    fun setup() {
        datasource = TaskLocalDatasource(mapper, dao)
    }

    @Test
    fun `given getTask, when called passing a task id, return the task with given id`() {
        every { mapper.fromEntity(any()) } returns task1
        every { dao.loadTask(1) } returns Single.just(entity1)

        datasource.getTask("1").test().assertValue(task1)
        verify { mapper.fromEntity(entity1) }
        verify { dao.loadTask(1) }
    }

    @Test
    fun `given getTasks, when called, return the list of tasks as a flow`() {
        val taskList = listOf(task1, task2)
        every { mapper.fromEntity(any()) } returnsMany taskList
        every { dao.loadTasks() } returns Flowable.just(listOf(entity1, entity2))

        datasource.getTasks().test().assertValue(taskList)
        verify { mapper.fromEntity(entity1) }
        verify { mapper.fromEntity(entity2) }
        verify { dao.loadTasks() }
    }

    @Test
    fun `given saveTask, when task is passed, then save it locally`() {
        every { dao.saveTask(any()) } returns Completable.complete()
        every { mapper.toEntity(any()) } returns entity1
        datasource.saveTask(task1).test().assertComplete()
        verify { dao.saveTask(entity1) }
        verify { mapper.toEntity(task1) }
    }

    @Test
    fun `given updateTask, when task is passed, then update it locally`() {
        every { dao.updateTask(any()) } returns Completable.complete()
        every { mapper.toEntity(any()) } returns entity1
        datasource.updateTask(task1).test().assertComplete()
        verify { dao.updateTask(entity1) }
        verify { mapper.toEntity(task1) }
    }

    @Test
    fun `given deleteTask, when task id is passed, then delete task with provided id`() {
        every { dao.deleteTask(2) } returns Completable.complete()
        datasource.deleteTask("2").test().assertComplete()
        verify { dao.deleteTask(2) }
    }

    companion object {
        private val entity1 = TaskEntity(1, "task one", false, 0L, 1, false)
        private val entity2 = TaskEntity(2, "task two", false, 0L, 1, false)
        private val task1 = Tasc("1", "task one", false, 0L, 1, false)
        private val task2 = Tasc("2", "task two", false, 0L, 1, false)
    }
}