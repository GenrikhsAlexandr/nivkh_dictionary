package com.aleksandrgenrikhs.nivkhdictionary.di.viewModel

import androidx.lifecycle.ViewModel
import com.aleksandrgenrikhs.nivkhdictionary.presentation.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelsBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun mainViewModel(viewModel: MainViewModel): ViewModel
}