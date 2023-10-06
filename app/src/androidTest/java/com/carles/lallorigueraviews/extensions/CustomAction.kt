package com.carles.lallorigueraviews.extensions

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click

object CustomAction {
    fun clickOfChildWithId(@IdRes childId: Int) = object : ViewAction {
        override fun getDescription() = "Click of child with Id"

        override fun getConstraints() = null

        override fun perform(uiController: UiController, view: View) {
            click().perform(uiController, view.findViewById(childId))
        }
    }
}