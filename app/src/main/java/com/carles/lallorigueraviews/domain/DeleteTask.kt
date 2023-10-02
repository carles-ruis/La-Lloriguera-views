package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteTask @Inject constructor(
    private val repository: TaskRepository,
    private val schedulers: AppSchedulers
) {

    fun execute(taskId: String): Completable =
        repository.deleteTask(taskId)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
}