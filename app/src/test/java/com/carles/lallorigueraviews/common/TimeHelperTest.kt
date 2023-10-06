package com.carles.lallorigueraviews.common

import junit.framework.TestCase
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Test

class TimeHelperTest {

    @Test
    fun `given getMinutesUntilTomorrowAtGivenHour, when it is from midnight to 9, then return 1440 + 540`() {
        val toDateTimeAtCurrentTime: (LocalDate) -> DateTime = { localDate ->
            localDate.toDateTimeAtStartOfDay()
        }
        val result = TimeHelper.getMinutesUntilTomorrowAtGivenHour(0, 9, toDateTimeAtCurrentTime)
        TestCase.assertEquals(1440 + 540, result)
    }

    @Test
    fun `given getMinutesUntilTomorrowAtGivenHour, when it is from 8 to 9, then return 1440 + 60`() {
        val toDateTimeAtCurrentTime: (LocalDate) -> DateTime = { localDate ->
            localDate.toDateTimeAtStartOfDay().plusHours(8)
        }
        val result = TimeHelper.getMinutesUntilTomorrowAtGivenHour(0, 9, toDateTimeAtCurrentTime)
        TestCase.assertEquals(1440 + 60, result)
    }

    @Test
    fun `given getMinutesUntilTomorrowAtGivenHour, when it is from 10 to 9, then return 1440 - 60`() {
        val toDateTimeAtCurrentTime: (LocalDate) -> DateTime = { localDate ->
            localDate.toDateTimeAtStartOfDay().plusHours(10)
        }
        val result = TimeHelper.getMinutesUntilTomorrowAtGivenHour(0, 9, toDateTimeAtCurrentTime)
        TestCase.assertEquals(1440 - 60, result)
    }

    @Test
    fun `given getDaysBetweenDates, when dates belong to the same days, then return 0`() {
        val start = 1694272522000L
        val end = 1694272526000L
        val result = TimeHelper.getDaysBetweenDates(start, end)
        TestCase.assertEquals(0, result)
    }

    @Test
    fun `given getDaysBetweenDates, when end is one day later from start, then return 1`() {
        val start = 1694272522038L
        val end = 1694347213000L
        val result = TimeHelper.getDaysBetweenDates(start, end)
        TestCase.assertEquals(1, result)
    }

    @Test
    fun `given getDaysBetweenDates, when end is one day before start, then return -1`() {
        val end = 1694272522038L
        val start = 1694347213000L
        val result = TimeHelper.getDaysBetweenDates(start, end)
        TestCase.assertEquals(-1, result)
    }
}
