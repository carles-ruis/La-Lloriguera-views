package com.carles.lallorigueraviews.data.remote

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

fun DatabaseReference.generateNodeId(): String {
    return System.currentTimeMillis().toString()
}

fun <T> DatabaseReference.getSingleValue(
    dataType: Class<T>,
    success: (T) -> Unit,
    failure: () -> Unit,
    cancel: (Exception) -> Unit
) {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue(dataType)
            if (value == null) failure() else success(value)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("DatabaseReference", "singleValueEvent:${error.message}")
            cancel(error.toException())
        }
    }
    addListenerForSingleValueEvent(listener)
}

fun <T> DatabaseReference.getFlowableListListener(
    dataType: Class<T>,
    success: (List<T>) -> Unit,
    cancel: (Exception) -> Unit
): ValueEventListener {
    return object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val list: List<T> = mutableListOf<T?>().apply {
                for (item in snapshot.children) {
                    add(item.getValue(dataType))
                }
            }.filterNotNull()
            success(list)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("DatabaseReference", "getFlowableListListener:${error.message}")
            cancel(error.toException())
        }
    }
}



