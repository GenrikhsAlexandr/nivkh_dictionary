package com.aleksandrgenrikhs.nivkhdictionary.domain

import android.media.MediaPlayer
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

    suspend fun updateWord() = repository.updateWords()

    suspend fun isFavorite(word: Word) = repository.isFavorite(word)

    suspend fun wordSounds(uri: String): ResultState<MediaPlayer?> = repository.initPlayer(uri)

    fun playerDestroy() = repository.playerDestroy()
}