package com.carles.lallorigueraviews.model

import com.carles.lallorigueraviews.common.TimeHelper
import com.carles.lallorigueraviews.common.TimeHelper.Companion.DAYS_TO_MILLIS

data class Tasc(
    val id: String?,
    val name: String,
    val isOneTime: Boolean,
    val lastDate: Long,
    val periodicity: Int,
    val notificationsOn: Boolean
) {

    val nextDate: Long
        get() = lastDate + periodicity * DAYS_TO_MILLIS

    val daysRemaining: Int
        get() = TimeHelper.getDaysBetweenDates(
            start = System.currentTimeMillis(),
            end = nextDate
        )

}