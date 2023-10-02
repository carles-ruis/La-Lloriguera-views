package com.carles.lallorigueraviews.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int?,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("is_one_time")
    val isOneTime: Boolean,

    @ColumnInfo("last_done")
    val lastDone: Long,

    @ColumnInfo("periodicity_days")
    val periodicity: Int,

    @ColumnInfo("notifications_on")
    val notificationsOn: Boolean
)
