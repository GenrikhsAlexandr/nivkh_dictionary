package com.aleksandrgenrikhs.nivkhdictionary.di

import android.app.Application
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs.EnglishFragment
import com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs.NivkhFragment
import com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs.RussianFragment
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
    fun inject(russianFragment: RussianFragment)

    fun inject(englishFragment: EnglishFragment)

    fun getWordRepository(): WordRepository

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}