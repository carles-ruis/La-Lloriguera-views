package com.carles.lallorigueraviews.di

import android.content.Context
import androidx.room.Room
import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskDatasource
import com.carles.lallorigueraviews.data.local.TaskDao
import com.carles.lallorigueraviews.data.local.TaskDatabase
import com.carles.lallorigueraviews.data.local.TaskLocalDatasource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestModule {

    @Provides
    @Singleton
    fun provideSchedulers(): AppSchedulers {
        return AppSchedulers(
            ui = AndroidSchedulers.mainThread(),
            io = AndroidSchedulers.mainThread(),
            new = AndroidSchedulers.mainThread(),
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(database: TaskDatabase): TaskDao {
        return database.taskDao()
    }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppBindModule::class]
)
abstract class TestBindModule() {

    @Binds
    @Singleton
    abstract fun provideDatasource(datasource: TaskLocalDatasource): TaskDatasource
}