package com.aleksandrgenrikhs.nivkhdictionary.domain

import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WordInteractor @Inject constructor(
    private val repository: WordRepository
) {
    fun getFavoritesWords(): Flow<List<Word>> = repository.getFavoritesWords()

    suspend fun saveFavoriteWord(word: Word) = repository.saveFavoriteWord(word)

    suspend fun deleteFavoriteWord(word: Word) = repository.deleteFavoriteWord(word)

    suspend fun getWordForStartApp(): ResultState<List<Word>> = repository.getWordStartApp()

    fun getWords(): Flow<List<Word>> = repository.getWords()

    suspend fun updateWord(): ResultState<List<Word>> = repository.updateWords()

    suspend fun isFavorite(word: Word): Boolean = repository.isFavorite(word)

    fun isNetWorkConnected():Boolean = repository.isNetWorkConnected()

    suspend fun isUrlExist(wordId: Int):Boolean = repository.isUrlExist(wordId)

    suspend fun initPlayer(url: String, isUrlExist: Boolean) =
        repository.initPlayer(url, isUrlExist)

    fun play() = repository.play()

    fun destroyPlayer() = repository.destroyPlayer()
}