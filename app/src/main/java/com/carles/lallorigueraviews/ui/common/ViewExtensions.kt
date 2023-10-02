package com.carles.lallorigueraviews.ui.common

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment

private const val DEFAULT_DEBOUNCE_TIME = 2_000L

fun View.setDebounceClickListener(action: () -> Unit) {
    setDebounceClickListener(action, DEFAULT_DEBOUNCE_TIME)
}

fun View.setDebounceClickListener(action: () -> Unit, debounceTime: Long) {
    var lastClickTime: Long = 0
    this.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View?) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            lastClickTime = SystemClock.elapsedRealtime()
            action()
        }
    })
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(message: Int) {
    showToast(getString(message))
}

fun Fragment.showKeyboard() {
    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
}