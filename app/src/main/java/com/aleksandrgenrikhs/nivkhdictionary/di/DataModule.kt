package com.aleksandrgenrikhs.nivkhdictionary.di

import androidx.lifecycle.ViewModel
import com.aleksandrgenrikhs.nivkhdictionary.data.MapperWord
import com.aleksandrgenrikhs.nivkhdictionary.data.WordRepositoryImpl
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs.TabsViewModel
import com.genrikhsaleksandr.savefeature.di.ViewModelKey
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
            mapperWord: MapperWord
        ): WordRepository {
            return WordRepositoryImpl(
                mapperWord
            )
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(TabsViewModel::class)
    fun tabsViewModel(viewModel: TabsViewModel): ViewModel
}