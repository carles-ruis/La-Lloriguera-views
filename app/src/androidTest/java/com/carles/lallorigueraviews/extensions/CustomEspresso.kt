package com.carles.lallorigueraviews.extensions

import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.carles.lallorigueraviews.MainActivity
import com.carles.lallorigueraviews.R
import org.hamcrest.CoreMatchers

object CustomEspresso {

    fun onNavigateUpView(@IdRes toolbarId: Int = R.id.main_toolbar): ViewInteraction =
        Espresso.onView(
            CoreMatchers.allOf(
                CoreMatchers.instanceOf(AppCompatImageButton::class.java),
                ViewMatchers.withParent(ViewMatchers.withId(toolbarId))
            )
        )

    fun checkToolbarText(text: String, @IdRes toolbarId: Int = R.id.main_toolbar) {
        Espresso.onView(ViewMatchers.withId(toolbarId))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(text))))
    }

    fun waitUntilDialogIsDisplayed(scenario: ActivityScenario<MainActivity>, timeout: Long = WAIT_TIMEOUT) {
        val endTime = System.currentTimeMillis() + timeout
        var displayed = false
        while (System.currentTimeMillis() < endTime && displayed.not()) {
            Thread.sleep(10)
            scenario.onActivity { activity ->
                displayed = activity.hasWindowFocus().not()
            }
        }
    }

    private const val WAIT_TIMEOUT = 1_000L

}