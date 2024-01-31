package com.aleksandrgenrikhs.nivkhdictionary.domain

import kotlinx.coroutines.flow.Flow

interface WordRepository {

    fun getFavoritesWords(): Flow<List<Word>>

    suspend fun getWords(): List<Word>

    fun getWordsFromDb(): Flow<List<Word>>

    suspend fun saveFavoriteWord(word: Word)

    suspend fun deleteFavoriteWord(word: Word)

    suspend fun getAndSaveWords(): List<Word>

    suspend fun isFavorite(word: Word): Boolean

    fun error(): Boolean
}