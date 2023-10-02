package com.carles.lallorigueraviews.ui.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.carles.lallorigueraviews.common.TimeHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CheckDelayedTasksWorkerExecutor @Inject constructor(@ApplicationContext private val context: Context) {

    fun enqueueWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val initialDelay = TimeHelper.getMinutesUntilTomorrowAtGivenHour(System.currentTimeMillis(), NOTIFICATION_HOUR).toLong()
        val periodicWork = PeriodicWorkRequestBuilder<CheckDelayedTasksWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .addTag(WORK_TAG)
            .setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            /* uniqueWorkName = */ WORK_NAME,
            /* existingPeriodicWorkPolicy = */ ExistingPeriodicWorkPolicy.KEEP,
            /* periodicWork = */ periodicWork
        )
    }

    companion object {
        private const val WORK_NAME = "CheckDelayedWork"
        private const val WORK_TAG = "TAG:CheckDelayedWork"
        private const val NOTIFICATION_HOUR = 9
    }
}