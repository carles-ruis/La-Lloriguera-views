package com.carles.lallorigueraviews

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.carles.lallorigueraviews.extensions.CustomEspresso
import com.carles.lallorigueraviews.extensions.CustomMatcher.withSize
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EditTaskFragmentTest {

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun editTaskFragment_checkContent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = context.getString(R.string.edit_task_title)
        val taskName = "Mega task"
        val newName = "Giga task"
        val deleteTitle = context.getString(R.string.edit_task_delete)
        val deleteMessage = context.getString(R.string.edit_task_delete_confirmation)
        val yes = context.getString(R.string.yes)
        val cancel = context.getString(R.string.cancel)

        // tasks screen
        onView(withId(R.id.tasks_recyclerview)).check(matches(withSize(0)))
        onView(withId(R.id.tasks_add_button)).perform(click())

        // new task screen
        onView(withId(R.id.task_form_name_text)).perform(typeText(taskName))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.new_task_save_button)).perform(click())

        // tasks screen
        onView(withId(R.id.task_name_text)).check(matches(withText(taskName)))
        onView(withId(R.id.task_edit_button)).perform(click())

        // edit task screen: edit name
        CustomEspresso.checkToolbarText(title)
        onView(withId(R.id.task_form_name_text))
            .check(matches(withText(taskName)))
            .perform(clearText())
            .perform(typeText(newName))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.edit_task_save_button)).perform(click())

        // tasks screen
        onView(withText(taskName)).check(doesNotExist())
        onView(withId(R.id.task_name_text)).check(matches(withText(newName)))
        onView(withId(R.id.task_edit_button)).perform(click())

        // edit task screen: delete
        onView(withId(R.id.task_form_name_text)).check(matches(withText(newName)))
        onView(withId(R.id.edit_task_delete_button)).perform(click())
        onView(withText(deleteTitle)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
        onView(withText(deleteMessage)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
        onView(withId(android.R.id.button2)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).inRoot(RootMatchers.isDialog()).perform(click())

        // tasks screen
        onView(withId(R.id.tasks_recyclerview)).check(matches(withSize(0)))
    }
}
