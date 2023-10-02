package com.carles.lallorigueraviews.ui.common

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.safeNavigate(navigation: () -> NavDirections) {
    try {
        navigate(navigation())
    } catch (e: IllegalArgumentException) {
        Log.e("NavController.safeNavigate", e.localizedMessage ?: "IllegalArgumentException at navigation")
    } catch (e: IllegalStateException) {
        Log.e("NavController.safeNavigate", e.localizedMessage ?: "IllegalStateException at navigation")
    }
}