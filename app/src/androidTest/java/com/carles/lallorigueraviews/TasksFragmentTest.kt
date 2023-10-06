package com.carles.lallorigueraviews

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.carles.lallorigueraviews.extensions.CustomAction.clickOfChildWithId
import com.carles.lallorigueraviews.extensions.CustomEspresso
import com.carles.lallorigueraviews.extensions.CustomMatcher.atPosition
import com.carles.lallorigueraviews.extensions.CustomMatcher.withSize
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TasksFragmentTest {

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun tasksFragment_checkContent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = context.getString(R.string.tasks_title)
        val oneTimeTaskName = "Task one time"
        val periodicTaskName = "Task periodic"
        val newName = "New periodic"
        val periodicity = "2"
        val defaultPeriodicity = "7"

        // tasks screen: check empty list
        CustomEspresso.checkToolbarText(title)
        onView(withId(R.id.tasks_empty_text)).check(matches(isDisplayed()))
        onView(withId(R.id.tasks_recyclerview)).check(matches(withSize(0)))

        // new tasks
        onView(withId(R.id.tasks_add_button)).perform(click())
        onView(withId(R.id.task_form_name_text)).perform(typeText(oneTimeTaskName))
        onView(withId(R.id.task_form_one_time_button)).perform(click())
        onView(withId(R.id.new_task_save_button)).perform(click())

        onView(withId(R.id.tasks_add_button)).perform(click())
        onView(withId(R.id.task_form_name_text)).perform(typeText(periodicTaskName))
        onView(withId(R.id.task_form_periodicity_input)).perform(click())
        onView(withText(periodicity)).inRoot(RootMatchers.isPlatformPopup()).perform(click())
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.new_task_save_button)).perform(click())

        // tasks screen: check list content
        onView(withId(R.id.tasks_empty_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tasks_recyclerview))
            .check(matches(withSize(2)))
            .check(matches(atPosition(0, R.id.task_name_text, withText(periodicTaskName))))
            .check(matches(atPosition(0, R.id.task_days_remaining_text, withText(periodicity))))
            .check(matches(atPosition(0, R.id.task_edit_button, withContentDescription(R.string.tasks_edit))))
            .check(matches(atPosition(0, R.id.task_mark_as_done_button, withContentDescription(R.string.tasks_mark_as_done))))
            .check(matches(atPosition(0, R.id.task_one_time_text, not(isDisplayed()))))
            .check(matches(atPosition(1, R.id.task_name_text, withText(oneTimeTaskName))))
            .check(matches(atPosition(1, R.id.task_days_remaining_text, withText(defaultPeriodicity))))
            .check(matches(atPosition(1, R.id.task_one_time_text, isDisplayed())))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, clickOfChildWithId(R.id.task_edit_button)
                )
            )

        // edit task
        onView(withId(R.id.task_form_name_text)).perform(ViewActions.clearText()).perform(typeText(newName))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.edit_task_save_button)).perform(click())

        // tasks screen: edited task
        onView(withId(R.id.tasks_recyclerview))
            .check(matches(withSize(2)))
            .check(matches(atPosition(0, R.id.task_name_text, withText(newName))))

        // tasks screen: mark tasks as done
        onView(withId(R.id.tasks_recyclerview))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    /* position = */ 1,
                    /* viewAction = */ clickOfChildWithId(R.id.task_mark_as_done_button)
                )
            )
        onView(withId(R.id.tasks_recyclerview)).check(matches(withSize(1)))
    }
}
