package com.aleksandrgenrikhs.nivkhdictionary.presentation

import com.aleksandrgenrikhs.nivkhdictionary.data.WordRepositoryImpl
import com.aleksandrgenrikhs.nivkhdictionary.domain.DictionaryInteractor

class DictinaryInteractorFactory {
    fun provideInteractor(): DictionaryInteractor {
        val repository = WordRepositoryImpl()
        return DictionaryInteractor(repository)
    }
}