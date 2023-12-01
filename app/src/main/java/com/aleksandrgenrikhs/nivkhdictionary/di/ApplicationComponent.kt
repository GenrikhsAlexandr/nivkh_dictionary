package com.aleksandrgenrikhs.nivkhdictionary.di

import android.app.Application
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs.NivkhFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        DataModule::class]
)
@Singleton
interface ApplicationComponent {

    fun inject(nivkhFragment: NivkhFragment)

    fun getWordRepository(): WordRepository

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}