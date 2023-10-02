package com.carles.lallorigueraviews.domain

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskRepository
import com.carles.lallorigueraviews.model.Tasc
import io.reactivex.Flowable
import javax.inject.Inject

class GetTasks @Inject constructor(
    private val repository: TaskRepository,
    private val schedulers: AppSchedulers
) {

    fun execute(): Flowable<List<Tasc>> =
        repository.getTasks().map { tasks ->
            tasks.sortedBy { it.daysRemaining }
        }.subscribeOn(schedulers.io).observeOn(schedulers.ui)
}