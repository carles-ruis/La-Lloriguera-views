package com.carles.lallorigueraviews.common

import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.Minutes

class TimeHelper {
    companion object {

        const val DAYS_TO_MILLIS = 24L * 60 * 60 * 1_000

        fun getDaysBetweenDates(start: Long, end: Long): Int {
            return Days.daysBetween(
                LocalDate(start).toDateTimeAtStartOfDay(),
                LocalDate(end).toDateTimeAtStartOfDay()
            ).days
        }

        fun getMinutesUntilTomorrowAtGivenHour(
            currentTime: Long,
            hours: Int,
            toDateTimeAtCurrentTime: (LocalDate) -> DateTime = { localDate -> localDate.toDateTimeAtCurrentTime() }
        ): Int {
            return Minutes.minutesBetween(
                toDateTimeAtCurrentTime(LocalDate(currentTime)),
                LocalDate(currentTime).toDateTimeAtStartOfDay().plusDays(1).plusHours(hours)
            ).minutes
        }
    }
}