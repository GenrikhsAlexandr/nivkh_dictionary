package com.aleksandrgenrikhs.nivkhdictionary.di

import android.app.Application
import com.aleksandrgenrikhs.nivkhdictionary.data.WordRepositoryImpl
import com.aleksandrgenrikhs.nivkhdictionary.data.database.AppDatabase
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDao
import com.aleksandrgenrikhs.nivkhdictionary.domain.NetworkConnectionChecker
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.utils.NetworkConnected
import com.aleksandrgenrikhs.nivkhdictionary.utils.WordMediaPlayer
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule::class,
    ]
)
interface AppModule {
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
        fun provideNetworkConnected(): NetworkConnectionChecker = NetworkConnected

        @Provides
        @Singleton
        fun provideMediaPlayer(): WordMediaPlayer = WordMediaPlayer
    }
}
