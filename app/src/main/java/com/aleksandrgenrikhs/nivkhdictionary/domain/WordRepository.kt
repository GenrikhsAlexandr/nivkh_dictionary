package com.aleksandrgenrikhs.nivkhdictionary.domain

import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface WordRepository {

    fun getFavoritesWords(): Flow<List<Word>>

    fun getWords(): Flow<List<Word>>

    suspend fun saveFavoriteWord(word: Word)

    suspend fun deleteFavoriteWord(word: Word)

    suspend fun getWordStartApp(): ResultState<List<Word>>

    suspend fun updateWords(): ResultState<List<Word>>

    suspend fun isFavorite(word: Word): Boolean

    fun isNetWorkConnected(): Boolean

    suspend fun isUrlExist(wordId: Int): Boolean
}