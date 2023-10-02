package com.carles.lallorigueraviews.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.carles.lallorigueraviews.common.NotificationHelper
import com.carles.lallorigueraviews.domain.GetTasks
import com.carles.lallorigueraviews.AppSchedulers
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class CheckDelayedTasksWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getTasks: GetTasks,
    private val notificationHelper: NotificationHelper,
    private val schedulers: AppSchedulers
) : RxWorker(context, params) {

    private val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    private fun createDelayedTasksNotification(tasks: List<String>) {
        notificationHelper.createDelayedTasksChannel()
        notificationHelper.createDelayedTasksNotification(tasks)
    }

    override fun createWork(): Single<Result> {
        return getTasks.execute().firstOrError()
            .doOnSuccess { tasks ->
                val delayedTasks: List<String> = tasks.filter { it.daysRemaining < 0 }.map { task -> task.name }
                if (delayedTasks.isNotEmpty()) {
                    createDelayedTasksNotification(delayedTasks)
                }
            }.map {
                Log.i("CheckDelayedTasksWorker", "executed at ${formatter.format(Date())}")
                Result.success()
            }.onErrorReturn { error ->
                Log.w("CheckDelayedTasksWorker", error.localizedMessage ?: "getTasks error")
                Result.failure()
            }.observeOn(schedulers.io)
    }
}