package com.carles.lallorigueraviews

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.EmptyResultSetException
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.carles.lallorigueraviews.data.local.TaskDao
import com.carles.lallorigueraviews.data.local.TaskDatabase
import com.carles.lallorigueraviews.data.local.TaskEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksDaoTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var dao: TaskDao
    private lateinit var database: TaskDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            context = InstrumentationRegistry.getInstrumentation().context,
            klass = TaskDatabase::class.java
        )
            .allowMainThreadQueries().build()
        dao = database.taskDao()
    }


    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun givenLoadTask_whenCalledWithId_thenReturnTaskWithThatId() {
        dao.saveTask(task1).test().assertComplete()
        dao.loadTask(taskId).test().assertValue(task1)
    }

    @Test
    fun givenLoadTask_whenTaskIsNotStored_thenEmitError()  {
        dao.loadTask(taskId).test().assertError(EmptyResultSetException::class.java)
    }

    @Test
    fun givenLoadTasks_whenCalled_thenReturnAllTasks() {
        val expected = listOf(task1, task2)
        dao.saveTask(task1).test().assertComplete()
        dao.saveTask(task2).test().assertComplete()
        dao.loadTasks().test().assertValue(expected)
    }

    @Test
    fun givenUpdateTask_whenTaskPassed_thenUpdateTaskInDatabase() {
        val newName = "super task"
        val updatedTask = task1.copy(name = newName)
        dao.saveTask(task1).test().assertComplete()
        dao.updateTask(updatedTask).test().assertComplete()
        dao.loadTask(taskId).test().assertValue(updatedTask)
    }

    @Test
    fun givenUpdateTask_whenTaskWasNotStored_thenDoNotStoreIt() {
        dao.updateTask(task1).test().assertComplete()
        dao.loadTask(taskId).test().assertError(EmptyResultSetException::class.java)
    }

    @Test
    fun givenDeleteTask_whenIdIsPassed_thenDeleteTaskWithThatId() {
        dao.saveTask(task1).test().assertComplete()
        dao.deleteTask(taskId).test().assertComplete()
        dao.loadTask(taskId).test().assertError(EmptyResultSetException::class.java)
    }

    companion object {
        private const val taskId = 1
        private val task1 = TaskEntity(taskId, "the task", false, 0L, 7, false)
        private val task2 = TaskEntity(2, "the task two", true, 0L, 7, false)
    }
}