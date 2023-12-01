package com.aleksandrgenrikhs.nivkhdictionary.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DictionaryInteractor @Inject constructor(
    private val repository: WordRepository
) {
    fun getFavoritesWords(): Flow<List<Word>> = repository.getFavoritesWords()

    fun getWordsCurrentLocale(locale: String): Flow<List<Word>> = repository
        .getWordsCurrentLocale(locale)

    suspend fun saveFavoriteWord(word: Word): Word = repository.saveFavoriteWord(word)

    suspend fun deleteFavoriteWord(word: Word) = repository.deleteFavoriteWord(word)

    suspend fun getWords(): List<Word> = repository.getWords()

    suspend fun updateWords(): List<Word> = repository.updateWords()
}