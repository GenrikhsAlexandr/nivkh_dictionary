package com.aleksandrgenrikhs.nivkhdictionary.data

import android.app.Application
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDao
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.utils.NetworkConnected
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val wordMapper: WordMapper,
    private val networkConnected: NetworkConnected,
    private val application: Application,
) : WordRepository {

    companion object {
        const val BASE_URL = "http://bibl-nogl-dictionary.ru"
    }

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(OkHttpClient.Builder().apply {
            addInterceptor(
                HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            )
        }.build()).build()

    private val service: WordService = retrofit.create(WordService::class.java)

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
            if (!networkConnected.isNetworkConnected(application)) {
                return@withContext ResultState.Error(R.string.error_message)
            } else {
                try {
                    val response = service.getWords().mapNotNull {
                        wordMapper.mapToWord(it)
                    }
                    response.forEach {
                        val allWords = wordMapper.mapWordToAllWordsDb(it)
                        wordDao.insertWord(allWords)
                    }
                    return@withContext ResultState.Success(response)
                } catch (e: Exception) {
                    ResultState.Error(R.string.error_message)
                }
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
}