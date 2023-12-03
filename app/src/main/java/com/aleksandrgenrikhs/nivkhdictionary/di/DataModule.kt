package com.aleksandrgenrikhs.nivkhdictionary.di

import android.app.Application
import androidx.lifecycle.ViewModel
import com.aleksandrgenrikhs.nivkhdictionary.data.WordMapper
import com.aleksandrgenrikhs.nivkhdictionary.data.WordRepositoryImpl
import com.aleksandrgenrikhs.nivkhdictionary.data.database.AppDatabase
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDao
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.presentation.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface DataModule {

    companion object {

        @Provides
        @Singleton
        fun provideWordRepository(
            wordMapper: WordMapper,
            wordDao: WordDao,
        ): WordRepository {
            return WordRepositoryImpl(
                wordDao,
                wordMapper,
            )
        }

        @Provides
        @Singleton
        fun provideWordDao(
            application: Application
        ): WordDao {
            return AppDatabase.getInstance(application).wordsRequestDao()
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun mainViewModel(viewModel: MainViewModel): ViewModel
}