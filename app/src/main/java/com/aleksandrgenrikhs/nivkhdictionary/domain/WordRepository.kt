package com.aleksandrgenrikhs.nivkhdictionary.domain

import kotlinx.coroutines.flow.Flow

interface WordRepository {

    fun getFavoritesWords(): Flow<List<Word>>

    suspend fun saveFavoriteWord(word: Word)

    suspend fun deleteFavoriteWord(word: Word)

    suspend fun getWords(): List<Word>

    suspend fun updateWords(): List<Word>

    suspend fun isFavorite(word: Word): Boolean

}