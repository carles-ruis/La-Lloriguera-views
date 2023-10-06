package com.carles.lallorigueraviews

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.carles.lallorigueraviews.extensions.CustomEspresso.checkToolbarText
import com.carles.lallorigueraviews.extensions.CustomEspresso.onNavigateUpView
import com.carles.lallorigueraviews.extensions.CustomMatcher.withSize
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activity = ActivityScenarioRule(MainActivity::class.java)

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun mainActivity_navigation() {
        val taskName = "menja llaminadures"
        val newName = "menja gelats"
        val tasksTitle = context.getString(R.string.tasks_title)
        val newTaskTitle = context.getString(R.string.new_task_title)
        val editTaskTitle = context.getString(R.string.edit_task_title)

        // tasks screen
        checkToolbarText(tasksTitle)
        onView(withId(R.id.tasks_add_button)).perform(click())

        // new task screen
        checkToolbarText(newTaskTitle)
        onView(withId(R.id.task_form_name_text)).perform(typeText(taskName))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.new_task_save_button)).perform(click())

        // tasks screen
        checkToolbarText(tasksTitle)
        onView(withId(R.id.tasks_recyclerview)).check(matches(withSize(1)))
        onView(withId(R.id.task_name_text)).check(matches(withText(taskName)))
        onView(withId(R.id.task_edit_button)).perform(click())

        // edit task screen
        checkToolbarText(editTaskTitle)
        onView(withId(R.id.task_form_name_text)).check(matches(withText(taskName)))
            .perform(clearText())
            .perform(typeText(newName))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.edit_task_save_button)).perform(click())

        // tasks screen
        checkToolbarText(tasksTitle)
        onView(withId(R.id.task_name_text)).check(matches(withText(newName)))
        onView(withId(R.id.task_edit_button)).perform(click())

        // edit task screen: up button
        checkToolbarText(editTaskTitle)
        onNavigateUpView().perform(click())

        // leave app
        Espresso.pressBackUnconditionally()
        val isResumed = activity.scenario.state == Lifecycle.State.RESUMED
        val isCreated = activity.scenario.state == Lifecycle.State.CREATED
        assertTrue(isResumed || isCreated)
    }
}