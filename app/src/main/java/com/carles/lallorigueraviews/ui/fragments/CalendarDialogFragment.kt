package com.carles.lallorigueraviews.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import org.joda.time.DateTime

class CalendarDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return with(requireArguments()) {
            DatePickerDialog(
                /* context = */ requireContext(),
                /* listener = */ { _, year, month, dayOfMonth ->
                    setFragmentResult(
                        requestKey = getString(EXTRA_REQUEST_KEY) ?: REQUEST_KEY_LAST_DATE,
                        result = bundleOf(RESULT_SELECTED_DAY to DateTime(year, month + 1, dayOfMonth, 1, 1).millis)
                    )
                },
                /* year = */ getInt(EXTRA_YEAR),
                /* month = */ getInt(EXTRA_MONTH) - 1,
                /* dayOfMonth = */ getInt(EXTRA_DAY)
            ).apply {
                datePicker.minDate = getLong(EXTRA_MIN_DATE)
                datePicker.maxDate = getLong(EXTRA_MAX_DATE)
            }
        }
    }

    companion object {
        const val REQUEST_KEY_LAST_DATE = "request_key_last_date"
        const val REQUEST_KEY_NEXT_DATE = "request_key_next_date"

        const val EXTRA_YEAR = "extraYear"
        const val EXTRA_MONTH = "extraMonth"
        const val EXTRA_DAY = "extraDay"
        const val EXTRA_MIN_DATE = "extraMinDate"
        const val EXTRA_MAX_DATE = "extraMaxDate"
        const val EXTRA_REQUEST_KEY = "extraRequestKey"
        const val RESULT_SELECTED_DAY = "resultSelectedDay"
    }
}