package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import com.carles.lallorigueraviews.model.Tasc
import io.reactivex.Completable
import javax.inject.Inject

class NewTask @Inject constructor(
    private val repository: TaskRepository,
    private val schedulers: AppSchedulers
) {

    fun execute(task: Tasc): Completable =
        repository.saveTask(task)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
}