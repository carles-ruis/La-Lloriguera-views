package com.carles.lallorigueraviews.data.remote

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TaskRef(
    val id: String? = null,
    val name: String? = null,
    val oneTime: Boolean? = null,
    val lastDone: Long? = null,
    val periodicity: Int? = null,
    val notificationsOn: Boolean? = null
)