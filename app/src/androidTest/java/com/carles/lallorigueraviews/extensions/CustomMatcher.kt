package com.carles.lallorigueraviews.extensions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

object CustomMatcher {

    fun withSize(size: Int) = object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("RecyclerView should have $size items")
        }

        override fun matchesSafely(recyclerView: RecyclerView): Boolean = recyclerView.adapter!!.itemCount == size
    }

    fun atPosition(position: Int, viewId: Int, matcher: Matcher<View>) =
        object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: $matcher")
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean =
                matcher.matches(recyclerView.findViewHolderForAdapterPosition(position)!!.itemView.findViewById(viewId))
        }
}