package com.carles.lallorigueraviews.data.local

import android.util.Log
import com.carles.lallorigueraviews.data.TaskDatasource
import com.carles.lallorigueraviews.data.TaskMapper
import com.carles.lallorigueraviews.model.Tasc
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class TaskLocalDatasource @Inject constructor(
    private val taskMapper: TaskMapper,
    private val taskDao: TaskDao
) : TaskDatasource {

    override fun getTask(id: String): Single<Tasc> {
        return taskDao.loadTask(id.toInt()).map { entity ->
            taskMapper.fromEntity(entity)
        }
    }

    override fun getTasks(): Flowable<List<Tasc>> {
        return taskDao.loadTasks().map { task ->
            task.map {
                taskMapper.fromEntity(it)
            }
        }
    }

    override fun saveTask(task: Tasc): Completable {
        return taskDao.saveTask(taskMapper.toEntity(task))
    }

    override fun updateTask(task: Tasc): Completable {
        return taskDao.updateTask(taskMapper.toEntity(task))
    }

    override fun deleteTask(id: String): Completable {
        return taskDao.deleteTask(id.toInt())
    }
}