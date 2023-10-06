package com.carles.lallorigueraviews

import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isFocused
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.carles.lallorigueraviews.extensions.CustomEspresso
import com.carles.lallorigueraviews.extensions.CustomEspresso.checkToolbarText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.joda.time.DateTime
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*


@HiltAndroidTest
class NewTaskFragmentTest {

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activity = ActivityScenarioRule(MainActivity::class.java)

    private val formatter = SimpleDateFormat("dd/MM/yyyy")

    @Test
    fun newTaskFragment_checkContent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = context.getString(R.string.new_task_title)
        val periodicTaskName = "Peri Task"
        val periodicity = "4"
        val defaultPeriodicity = "7"
        val oneTimeTaskName = "Once Task"
        val today = DateTime(Date())
        val tomorrow = DateTime(Date()).plusDays(1)
        val sevenDaysAfter = DateTime(Date()).plusDays(7)

        // tasks screen
        onView(withId(R.id.tasks_add_button)).perform(click())

        // new task: empty name error
        checkToolbarText(title)
        onView(withText(R.string.new_task_name_error)).check(doesNotExist())
        onView(withText(R.string.new_task_name_description)).check(matches(isDisplayed()))
        onView(withId(R.id.new_task_save_button)).perform(click())
        onView(withId(R.id.task_form_name_text)).check(matches(isFocused()))
        onView(withText(R.string.new_task_name_error)).check(matches(isDisplayed()))
        onView(withText(R.string.new_task_name_description)).check(matches(not(isDisplayed())))

        // new task: fill periodic task
        onView(withId(R.id.task_form_name_text)).perform(typeText(periodicTaskName))
        onView(withId(R.id.task_one_time_date_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.task_form_periodic_date_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_form_periodicity_text)).check(matches(withText(defaultPeriodicity))).perform(click())
        onView(withText(periodicity)).inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withId(R.id.task_form_periodic_date_text))
            .check(matches(withText(formatter.format(today.millis))))
            .perform(click())

        with(tomorrow) {
            onView(withClassName(equalTo(DatePicker::class.java.name)))
                .perform(PickerActions.setDate(year, monthOfYear, dayOfMonth))
        }
        onView(withText(android.R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(android.R.string.ok)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.task_form_periodic_date_text)).check(matches(withText(formatter.format(tomorrow.millis))))
        onView(withId(R.id.new_task_save_button)).perform(click())
        onView(withId(R.id.tasks_add_button)).perform(click())

        // fill one time task form
        onView(withId(R.id.task_form_name_text)).perform(typeText(oneTimeTaskName))
        onView(withId(R.id.task_form_one_time_button)).perform(click())
        onView(withId(R.id.task_one_time_date_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_form_periodic_date_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.task_form_periodicity_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.task_one_time_date_text)).check(matches(withText(formatter.format(sevenDaysAfter.millis))))
        onView(withId(R.id.task_one_time_date_text)).perform(click())

        CustomEspresso.waitUntilDialogIsDisplayed(activity.scenario)
        with(tomorrow) {
            onView(withClassName(equalTo(DatePicker::class.java.name)))
                .perform(PickerActions.setDate(year, monthOfYear, dayOfMonth))
        }
        onView(withText(android.R.string.ok)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.task_one_time_date_text)).check(matches(withText(formatter.format(tomorrow.millis))))
        onView(withId(R.id.new_task_save_button)).perform(click())
    }
}