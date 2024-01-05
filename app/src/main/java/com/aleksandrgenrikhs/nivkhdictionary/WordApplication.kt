package com.aleksandrgenrikhs.nivkhdictionary

import android.app.Application
import com.aleksandrgenrikhs.nivkhdictionary.di.ApplicationComponent
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.DaggerApplicationComponent

class WordApplication : Application(), ComponentProvider {

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun provideComponent(): ApplicationComponent {
        return applicationComponent
    }
}