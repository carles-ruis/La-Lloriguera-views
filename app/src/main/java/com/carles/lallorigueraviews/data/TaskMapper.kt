package com.carles.lallorigueraviews.data

import com.carles.lallorigueraviews.data.local.TaskEntity
import com.carles.lallorigueraviews.model.Tasc
import javax.inject.Inject

class TaskMapper @Inject constructor() {

    fun fromEntity(entity: TaskEntity): Tasc = with(entity) {
        Tasc(id?.toString(), name, isOneTime, lastDone, periodicity, notificationsOn)
    }

    fun toEntity(model: Tasc): TaskEntity = with(model) {
        TaskEntity(id?.toInt(), name, isOneTime, lastDate, periodicity, notificationsOn)
    }
}