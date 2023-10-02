package com.carles.lallorigueraviews.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

abstract class TaskFragment: Fragment() {

    abstract fun initialize()

    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}