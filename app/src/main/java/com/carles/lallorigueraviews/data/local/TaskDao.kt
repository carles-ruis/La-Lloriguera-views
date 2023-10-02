package com.carles.lallorigueraviews.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface TaskDao {

    @Query("select * from task where _id=:id")
    fun loadTask(id: Int): Single<TaskEntity>

    @Query("select * from task")
    fun loadTasks(): Flowable<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTask(task: TaskEntity): Completable

    @Update
    fun updateTask(task: TaskEntity): Completable

    @Query("delete from task where _id=:id")
    fun deleteTask(id: Int): Completable

}