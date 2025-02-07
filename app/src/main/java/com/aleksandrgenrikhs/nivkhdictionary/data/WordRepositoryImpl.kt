package com.aleksandrgenrikhs.nivkhdictionary.data

import android.app.Application
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDao
import com.aleksandrgenrikhs.nivkhdictionary.domain.NetworkConnectionChecker
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import com.aleksandrgenrikhs.nivkhdictionary.utils.WordMediaPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val wordMapper: WordMapper,
    private val networkConnected: NetworkConnectionChecker,
    private val application: Application,
    private val service: ApiService,
    private val mediaPlayer: WordMediaPlayer
) : WordRepository {

    companion object {
        const val BASE_URL = "http://bibl-nogl-dictionary.ru"
    }

    override suspend fun getWordStartApp(): ResultState<List<Word>> {
        val wordsFromDb = wordDao.getWords().firstOrNull()
        if (wordsFromDb != null) {
            if (wordsFromDb.isEmpty()) {
                return updateWords()
            }
        }
        return ResultState.Success(emptyList())
    }

    override suspend fun updateWords(): ResultState<List<Word>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getWords().mapNotNull {
                    wordMapper.mapToWord(it)
                }
                val entities = response.map {
                    wordMapper.mapWordToAllWordsDb(it)
                }
                wordDao.deleteAllWords()
                wordDao.insertWords(entities)
                return@withContext ResultState.Success(response)
            } catch (e: Exception) {
                ResultState.Error(R.string.error_message_response)
            }
        }
    }

    override fun getWords(): Flow<List<Word>> {
        return wordDao.getWords().map { listWordsDb ->
            listWordsDb.map {
                wordMapper.mapWordEntityToWord(it)
            }
        }
    }

    override fun getFavoritesWords(): Flow<List<Word>> {
        return wordDao.getFavorites().map { listWordDBMFavorites ->
            listWordDBMFavorites.map {
                wordMapper.mapFavoriteWordEntityToWord(it)
            }
        }
    }

    override suspend fun saveFavoriteWord(word: Word) {
        val wordDbFavorites = wordMapper.mapWordToFavoriteWordEntity(word)
        wordDao.insertFavoriteWord(wordDbFavorites)
    }

    override suspend fun deleteFavoriteWord(word: Word) {
        val wordDbModel = wordMapper.mapWordToFavoriteWordEntity(word)
        wordDao.deleteWord(wordDbModel)
    }

    override suspend fun isFavorite(word: Word): Boolean {
        val wordDbFavorite = wordDao.getWordById(word.id)
        return wordDbFavorite != null
    }

    override fun isNetWorkConnected(): Boolean {
        return networkConnected.isNetworkConnected(application)
    }

    override suspend fun isUrlExist(wordId: Int): Boolean {
        return try {
            service.getAudioWord(wordId).isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun initPlayer(url: String, isUrlExist: Boolean) {
        mediaPlayer.initPlayer(application, url, isUrlExist)
    }

    override fun play() {
        mediaPlayer.play()
    }

    override fun destroyPlayer() {
        mediaPlayer.destroyPlayer()
    }
}