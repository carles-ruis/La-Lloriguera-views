package com.carles.lallorigueraviews.data.remote

import com.carles.lallorigueraviews.data.TaskMapper
import com.carles.lallorigueraviews.model.Tasc
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class TaskRemoteDatasourceTest {

    private val database: DatabaseReference = mockk()
    private val mapper: TaskMapper = mockk()
    private val tasksNode: DatabaseReference = mockk()
    private val taskNode: DatabaseReference = mockk()
    private val databaseExtensions = "com.carles.lallorigueraviews.data.remote.DatabaseExtensionsKt"
    private val task: Task<Void> = mockk()
    private lateinit var datasource: TaskRemoteDatasource

    @Before
    fun setup() {
        datasource = TaskRemoteDatasource(database, mapper)
        mockkStatic(databaseExtensions)
    }

    @Test
    fun `given getTask, when id is provided, then obtain task from the remote database`() {
        every { database.waitForConnection(any()) } returns Completable.complete()
        every { database.child(any()) } returns tasksNode
        every { tasksNode.child(any()) } returns taskNode
        every { taskNode.getSingleValue(any<Class<TaskRef>>()) } returns Single.just(taskRef1)
        every { mapper.fromRef(any<TaskRef>()) } returns task1

        datasource.getTask(taskId).test().assertValue(task1)

        verify { database.waitForConnection() }
        verify { database.child(TASKS_NODE) }
        verify { tasksNode.child(taskId) }
        verify { taskNode.getSingleValue(TaskRef::class.java) }
        verify { mapper.fromRef(taskRef1) }
    }

    @Test
    fun `given getTasks, when called, then return list of tasks as a flow`() {
        val tasksRef = listOf(taskRef1, taskRef2)
        val tasks = listOf(task1, task2)
        every { database.waitForConnection(any()) } returns Completable.complete()
        every { database.child(TASKS_NODE) } returns tasksNode
        every { tasksNode.getFlowableList(TaskRef::class.java) } returns Flowable.just(tasksRef)
        every { mapper.fromRef(any<List<TaskRef>>()) } returns tasks

        datasource.getTasks().test().assertValue(tasks)

        verify { database.waitForConnection() }
        verify { database.child(TASKS_NODE) }
        verify { tasksNode.getFlowableList(TaskRef::class.java) }
        verify { mapper.fromRef(tasksRef) }
    }

    @Test
    fun `given saveTask, when task is provided, then save it to firebase database`() {
        every { database.waitForConnection() } returns Completable.complete()
        every { database.generateNodeId() } returns taskId
        every { mapper.toRef(any()) } returns taskRef1
        every { database.child(any()) } returns tasksNode
        every { tasksNode.child(any()) } returns taskNode
        every { taskNode.setValue(any()) } returns task
        every { task.getCompletableValue() } returns Completable.complete()

        datasource.saveTask(task1).test().assertComplete()

        verify { database.waitForConnection() }
        verify { database.generateNodeId() }
        verify { mapper.toRef(task1) }
        verify { database.child(TASKS_NODE) }
        verify { tasksNode.child(taskId) }
        verify { taskNode.setValue(taskRef1) }
        verify { task.getCompletableValue() }
    }

    @Test
    fun `given updateTask, when task is provided, then update it in firebase database`() {
        every { database.waitForConnection() } returns Completable.complete()
        every { mapper.toRef(any()) } returns taskRef1
        every { database.child(any()) } returns tasksNode
        every { tasksNode.child(any()) } returns taskNode
        every { taskNode.setValue(any()) } returns task
        every { task.getCompletableValue() } returns Completable.complete()

        datasource.updateTask(task1).test().assertComplete()

        verify { database.waitForConnection() }
        verify { mapper.toRef(task1) }
        verify { database.child(TASKS_NODE) }
        verify { tasksNode.child(taskId) }
        verify { taskNode.setValue(taskRef1) }
        verify { task.getCompletableValue() }
    }

    @Test
    fun `given deleteTask, when id is provided, then delete task with provided id`() {
        every { database.waitForConnection() } returns Completable.complete()
        every { database.child(any()) } returns tasksNode
        every { tasksNode.child(any()) } returns taskNode
        every { taskNode.removeValue() } returns task
        every { task.getCompletableValue() } returns Completable.complete()

        datasource.deleteTask(taskId).test().assertComplete()

        verify { database.waitForConnection() }
        verify { database.child(TASKS_NODE) }
        verify { tasksNode.child(taskId) }
        verify { taskNode.removeValue() }
        verify { task.getCompletableValue() }
    }

    companion object {
        private const val taskId = "1"
        private val taskRef1 = TaskRef(taskId, "my task", false, 0L, 7, false)
        private val task1 = Tasc(taskId, "my task", false, 0L, 7, false)
        private val taskRef2 = TaskRef("2", "my task two", false, 0L, 7, false)
        private val task2 = Tasc("2", "my task two", false, 0L, 7, false)

        private const val TASKS_NODE = "tasks"
    }

}