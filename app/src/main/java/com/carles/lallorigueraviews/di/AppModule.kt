package com.carles.lallorigueraviews.di

import com.carles.lallorigueraviews.AppSchedulers
import com.carles.lallorigueraviews.data.TaskDatasource
import com.carles.lallorigueraviews.data.remote.TaskRemoteDatasource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSchedulers(): AppSchedulers {
        return AppSchedulers(
            ui = AndroidSchedulers.mainThread(),
            io = Schedulers.io(),
            new = Schedulers.newThread()
        )
    }

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference {
        // add local URL for testing with an emulator
        return Firebase.database.reference
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindModule() {

    @Binds
    @Singleton
    abstract fun provideDatasource(datasource: TaskRemoteDatasource): TaskDatasource
}

