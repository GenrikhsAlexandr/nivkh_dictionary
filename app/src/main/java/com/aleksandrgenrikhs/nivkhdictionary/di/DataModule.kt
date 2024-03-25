package com.aleksandrgenrikhs.nivkhdictionary.di

import android.app.Application
import com.aleksandrgenrikhs.nivkhdictionary.data.WordRepositoryImpl
import com.aleksandrgenrikhs.nivkhdictionary.data.database.AppDatabase
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDao
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.utils.NetworkConnected
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface DataModule {
    @Binds
    @Singleton
    fun bindsWordRepository(impl: WordRepositoryImpl): WordRepository

    companion object {

        @Provides
        @Singleton
        fun provideWordDao(
            application: Application
        ): WordDao = AppDatabase.getInstance(application).wordsRequestDao()

        @Provides
        @Singleton
        fun networkConnected(): NetworkConnected = NetworkConnected

        /*    @Provides
            @Singleton
            fun provideSearchRepository(): SearchRepository = SearchRepository*/
    }

}
