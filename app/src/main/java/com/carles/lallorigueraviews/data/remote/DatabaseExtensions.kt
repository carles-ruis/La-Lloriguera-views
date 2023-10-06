package com.carles.lallorigueraviews.data.remote

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

private const val DEFAULT_TIMEOUT = 20_000L

fun DatabaseReference.waitForConnection(timeout: Long = DEFAULT_TIMEOUT): Completable {
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
        database.getReference(".info/connected").addValueEventListener(listener)

    }.timeout(timeout, TimeUnit.MILLISECONDS) { emitter ->
        emitter.onError(NoConnectionException())
    }
}

fun DatabaseReference.generateNodeId(): String {
    return System.currentTimeMillis().toString()
}

fun <T> DatabaseReference.getSingleValue(dataType: Class<T>): Single<T> {
    return Single.create { emitter ->
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(dataType)
                if (value == null) {
                    val errorMessage = "getSingleValue:data is not a ${dataType.simpleName} object"
                    Log.w("DatabaseReference", errorMessage)
                    emitter.onError(BadDatabaseReferenceException(errorMessage))
                } else {
                    emitter.onSuccess(value)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("DatabaseReference", "getSingleValue:${error.message}")
                emitter.onError(error.toException())
            }
        }
        addListenerForSingleValueEvent(listener)
    }
}

fun Task<Void>.getCompletableValue(): Completable {
    return Completable.create { emitter ->
        addOnSuccessListener { _ -> emitter.onComplete() }
        addOnFailureListener { exception ->
            Log.w("Task", "getCompletableValue:$exception.message")
            emitter.onError(exception)
        }
    }
}

fun <T> DatabaseReference.getFlowableList(dataType: Class<T>): Flowable<List<T>> {
    return Flowable.create({ emitter ->
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: List<T> = mutableListOf<T?>().apply {
                    for (item in snapshot.children) {
                        add(item.getValue(dataType))
                    }
                }.filterNotNull()
                emitter.onNext(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("DatabaseReference", "getFlowableList:${error.message}")
                emitter.onError(error.toException())
            }
        }
        addValueEventListener(listener)
        emitter.setCancellable { removeEventListener(listener) }
    }, BackpressureStrategy.LATEST)
}

