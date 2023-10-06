package com.carles.lallorigueraviews.domain

import android.util.Log
import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import com.carles.lallorigueraviews.model.Tasc
import io.reactivex.Completable
import javax.inject.Inject

class MarkTaskAsDone @Inject constructor(
    private val repository: TaskRepository,
    private val schedulers: AppSchedulers
) {

    fun execute(task: Tasc): Completable =
        if (task.isOneTime) {
            repository.deleteTask(task.id!!)
        } else {
            repository.updateTask(task.copy(lastDate = System.currentTimeMillis()))
        }.subscribeOn(schedulers.io).observeOn(schedulers.ui).doOnComplete {
            Log.e("carles","MarkTaskAsDone")
        }
}