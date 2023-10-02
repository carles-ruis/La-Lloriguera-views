package com.carles.lallorigueraviews.data

import com.carles.lallorigueraviews.model.Tasc
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface TaskDatasource {

    fun getTask(id: String): Single<Tasc>

    fun getTasks(): Flowable<List<Tasc>>

    fun saveTask(task: Tasc): Completable

    fun updateTask(task: Tasc): Completable

    fun deleteTask(id: String): Completable
}