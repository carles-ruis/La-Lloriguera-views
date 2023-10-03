package com.carles.lallorigueraviews.data.remote

import android.util.Log
import com.carles.lallorigueraviews.data.TaskDatasource
import com.carles.lallorigueraviews.data.TaskMapper
import com.carles.lallorigueraviews.model.Tasc
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val DEFAULT_TIMEOUT = 20_000L

class TaskRemoteDatasource @Inject constructor(
    private val database: DatabaseReference,
    private val taskMapper: TaskMapper
) : TaskDatasource {

    override fun getTask(id: String): Single<Tasc> {
        return waitForConnection().andThen(
            Single.defer {
                Single.create { emitter ->
                    val reference = database.child(TASKS_NODE).child(id)
                    reference.getSingleValue(
                        dataType = TaskRef::class.java,
                        success = { taskRef -> emitter.onSuccess(taskMapper.fromRef(taskRef)) },
                        failure = { emitter.onError(BadDatabaseReferenceException("getTask:data is not a TaskRef object")) },
                        cancel = { exception -> emitter.onError(exception) }
                    )
                }
            })
    }

    override fun getTasks(): Flowable<List<Tasc>> {
        return waitForConnection().andThen(
            Flowable.defer {
                Flowable.create({ emitter ->
                    val reference = database.child(TASKS_NODE)
                    val listener = reference.getFlowableListListener(
                        dataType = TaskRef::class.java,
                        success = { list -> emitter.onNext(taskMapper.fromRef(list)) },
                        cancel = { exception -> emitter.onError(exception) }
                    )
                    reference.addValueEventListener(listener)
                    emitter.setCancellable { reference.removeEventListener(listener) }
                }, BackpressureStrategy.LATEST)
            })
    }

    override fun saveTask(task: Tasc): Completable {
        return waitForConnection().andThen(
            Completable.defer {
                Completable.create { emitter ->
                    val taskId = database.generateNodeId()
                    val taskRef = taskMapper.toRef(task).copy(id = taskId)
                    database.child(TASKS_NODE).child(taskId)
                        .setValue(taskRef)
                        .addOnSuccessListener { _ -> emitter.onComplete() }
                        .addOnFailureListener { exception ->
                            Log.w("TaskRemoteDatasource", "saveTask:$exception.message")
                            emitter.onError(exception)
                        }
                }
            })
    }

    override fun updateTask(task: Tasc): Completable {
        return waitForConnection().andThen(
            Completable.defer {
                Completable.create { emitter ->
                    if (task.id == null) {
                        val errorMessage = "Error updating task. Task id not set"
                        Log.w("TaskRemoteDatasource", errorMessage)
                        emitter.onError(RuntimeException(errorMessage))
                    } else {
                        val taskRef = taskMapper.toRef(task)
                        database.child(TASKS_NODE).child(task.id)
                            .setValue(taskRef)
                            .addOnCompleteListener { _ -> emitter.onComplete() }
                            .addOnFailureListener { exception ->
                                Log.w("TaskRemoteDatasource", "updateTask:$exception.message")
                                emitter.onError(exception)
                            }
                    }
                }
            })
    }

    override fun deleteTask(id: String): Completable {
        return waitForConnection().andThen(
            Completable.defer {
                Completable.create { emitter ->
                    database.child(TASKS_NODE).child(id).removeValue()
                        .addOnCompleteListener { _ -> emitter.onComplete() }
                        .addOnFailureListener { exception ->
                            Log.w("TaskRemoteDatasource", "deleteTask:${exception.message}")
                            emitter.onError(exception)
                        }
                }
            })
    }

    private fun waitForConnection(timeout: Long = DEFAULT_TIMEOUT): Completable {
        return Completable.create { emitter ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isConnected = snapshot.getValue(Boolean::class.java)
                    if (isConnected == true) {
                        emitter.onComplete()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TaskRemoteDatasource", "waitForConnection:${error.message}")
                    emitter.onError(error.toException())
                }
            }
            database.database.getReference(".info/connected").addValueEventListener(listener)

        }.timeout(timeout, TimeUnit.MILLISECONDS) { emitter ->
            emitter.onError(NoConnectionCancellationException())
        }
    }

    companion object {
        private const val TASKS_NODE = "tasks"
    }
}
