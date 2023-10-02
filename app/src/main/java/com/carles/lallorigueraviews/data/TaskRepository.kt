package com.carles.lallorigueraviews.data

import com.carles.lallorigueraviews.model.Tasc
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class TaskRepository @Inject constructor(private val datasource: TaskDatasource) {

    fun getTask(id: String): Single<Tasc> {
        return datasource.getTask(id)
    }

    fun getTasks(): Flowable<List<Tasc>> {
        return datasource.getTasks()
    }

    fun saveTask(task: Tasc): Completable {
        return datasource.saveTask(task)
    }

    fun updateTask(task: Tasc): Completable {
        return datasource.updateTask(task)
    }

    fun deleteTask(id: String): Completable {
        return datasource.deleteTask(id)
    }
}