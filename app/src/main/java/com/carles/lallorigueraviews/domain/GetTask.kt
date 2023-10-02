package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import com.carles.lallorigueraviews.model.Tasc
import io.reactivex.Single
import javax.inject.Inject

class GetTask @Inject constructor(
    private val repository: TaskRepository,
    private val schedulers: AppSchedulers
) {

    fun execute(taskId: String): Single<Tasc> =
        repository.getTask(taskId)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
}