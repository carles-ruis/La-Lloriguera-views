package com.carles.lallorigueraviews

import android.widget.DatePicker
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.carles.lallorigueraviews.extensions.CustomEspresso
import com.carles.lallorigueraviews.extensions.CustomMatcher.withSize
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.equalTo
import org.joda.time.DateTime
import org.junit.Rule
import org.junit.Test
import java.util.*

@HiltAndroidTest
class ConillFragmentTest {

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun conillFragment_checkContent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = context.getString(R.string.conill_title)
        val taskName = "conill task"
        val conillImageDescription = context.getString(R.string.conill_image)
        val conillText = context.getString(R.string.conill_text)
        val twoWeeksAgo = DateTime(Date()).minusWeeks(2)
        val defaultPeriodicity = "7"

        // tasks screen
        onView(withId(R.id.tasks_recyclerview)).check(matches(withSize(0)))
        onView(withId(R.id.tasks_add_button)).perform(click())

        // new task screen: create delayed task
        onView(withId(R.id.task_form_name_text)).perform(typeText(taskName))
        onView(withId(R.id.task_form_periodic_date_text)).perform(click())
        with(twoWeeksAgo) {
            onView(withClassName(equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(year, monthOfYear, dayOfMonth)
            )
        }
        onView(withText(android.R.string.ok)).perform(click())
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.new_task_save_button)).perform(click())

        // tasks screen
        onView(withId(R.id.tasks_recyclerview)).check(matches(withSize(1)))
        onView(withId(R.id.task_mark_as_done_button)).perform(click())

        // conill screen should appear
        CustomEspresso.checkToolbarText(title)
        onView(withContentDescription(conillImageDescription)).check(matches(isDisplayed()))
        onView(withText(conillText)).check(matches(isDisplayed()))
        onView(withId(R.id.conill_ok_button)).perform(click())

        // tasks screen
        onView(withId(R.id.task_days_remaining_text)).check(matches(withText(defaultPeriodicity)))
    }
}