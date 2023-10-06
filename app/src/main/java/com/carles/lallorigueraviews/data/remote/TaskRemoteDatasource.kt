package com.carles.lallorigueraviews.data.remote

import android.util.Log
import com.carles.lallorigueraviews.data.TaskDatasource
import com.carles.lallorigueraviews.data.TaskMapper
import com.carles.lallorigueraviews.model.Tasc
import com.google.firebase.database.DatabaseReference
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class TaskRemoteDatasource @Inject constructor(
    private val database: DatabaseReference,
    private val taskMapper: TaskMapper
) : TaskDatasource {

    override fun getTask(id: String): Single<Tasc> {
        return database.waitForConnection().andThen(
            Single.defer {
                database.child(TASKS_NODE).child(id)
                    .getSingleValue(TaskRef::class.java)
                    .map { taskRef -> taskMapper.fromRef(taskRef) }
            })
    }

    override fun getTasks(): Flowable<List<Tasc>> {
        return database.waitForConnection().andThen(
            Flowable.defer {
                database.child(TASKS_NODE).getFlowableList(TaskRef::class.java)
                    .map { list -> taskMapper.fromRef(list) }
            })
    }

    override fun saveTask(task: Tasc): Completable {
        return database.waitForConnection().andThen(
            Completable.defer {
                val taskId = database.generateNodeId()
                val taskRef = taskMapper.toRef(task).copy(id = taskId)
                database.child(TASKS_NODE)
                    .child(taskId)
                    .setValue(taskRef)
                    .getCompletableValue()
            })
    }

    override fun updateTask(task: Tasc): Completable {
        return database.waitForConnection().andThen(
            Completable.defer {
                if (task.id == null) {
                    val errorMessage = "Error updating task. Task id not set"
                    Log.w("TaskRemoteDatasource", errorMessage)
                    Completable.error(RuntimeException(errorMessage))
                } else {
                    val taskRef = taskMapper.toRef(task)
                    database.child(TASKS_NODE).child(task.id)
                        .setValue(taskRef)
                        .getCompletableValue()
                }
            }
        )
    }

    override fun deleteTask(id: String): Completable {
        return database.waitForConnection().andThen(
            Completable.defer {
                database.child(TASKS_NODE).child(id)
                    .removeValue()
                    .getCompletableValue()
            }
        )
    }

    companion object {
        private const val TASKS_NODE = "tasks"
    }
}
