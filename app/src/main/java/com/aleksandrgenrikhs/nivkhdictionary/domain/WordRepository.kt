package com.aleksandrgenrikhs.nivkhdictionary.domain

import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDbFavorites
import kotlinx.coroutines.flow.Flow

interface WordRepository {

    fun getFavoritesWords(): Flow<List<Word>>

    fun getWordsFromDb(): Flow<List<Word>>

    suspend fun saveFavoriteWord(word: Word)

    suspend fun deleteFavoriteWord(word: Word)
    suspend fun deleteAllWord()

    suspend fun getAndSaveWords(): List<Word>

    suspend fun isFavorite(word: Word): Boolean

    fun getCountWords(): Flow<List<WordDbFavorites>>

}