package com.aleksandrgenrikhs.nivkhdictionary.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WordInteractor @Inject constructor(
    private val repository: WordRepository
) {
    fun getFavoritesWords(): Flow<List<Word>> = repository.getFavoritesWords()

    suspend fun saveFavoriteWord(word: Word): Word = repository.saveFavoriteWord(word)

    suspend fun deleteFavoriteWord(word: Word) = repository.deleteFavoriteWord(word)

    suspend fun getWords(): List<Word> = repository.getWords()

    suspend fun updateWords(): List<Word> = repository.updateWords()
}