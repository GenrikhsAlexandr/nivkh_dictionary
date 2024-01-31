package com.aleksandrgenrikhs.nivkhdictionary.data

import android.app.Application
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDao
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.aleksandrgenrikhs.nivkhdictionary.utils.NetworkConnected
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        private const val BASE_URL = "http://bibl-nogl-dictionary.ru"
    }

    private val _error: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val error: StateFlow<Boolean> = _error


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

    override suspend fun getAndSaveWords(): List<Word> {
        return withContext(Dispatchers.IO) {
            val wordsFromDb = wordDao.getWordsFromDb().firstOrNull()
            wordsFromDb?.let { words ->
                if (words.isNotEmpty()) {
                    println("isNotEmpty")
                    // Если в базе данных есть слова, возвращаем их
                    return@withContext words.map {
                        wordMapper.mapAllWordsDbToWord(it)
                    }
                }
            }
            _error.value = !networkConnected.isNetworkConnected(application)
            try {
                val response = service.getWords().mapNotNull {
                    wordMapper.mapToWord(it)
                }
                response.forEach {
                    val allWords = wordMapper.mapWordToAllWordsDb(it)
                    wordDao.insertAllWord(allWords)
                }

                return@withContext response

            } catch (e: Exception) {
                return@withContext emptyList()
            }
        }
    }

    override fun getWordsFromDb(): Flow<List<Word>> {
        return wordDao.getWordsFromDb().map { listWordsDb ->
            listWordsDb.map {
                wordMapper.mapAllWordsDbToWord(it)
            }
        }
    }

    override fun getFavoritesWords(): Flow<List<Word>> {
        return wordDao.getFavorites().map { listWordDBMFavorites ->
            listWordDBMFavorites.map {
                wordMapper.mapWordDbFavoritesToWord(it)
            }
        }
    }

    override suspend fun getWords(): List<Word> = withContext(Dispatchers.IO) {
        try {
            println("getWords")

            val response = service.getWords().mapNotNull {
                wordMapper.mapToWord(it)
            }
            response.forEach {
                val allWords = wordMapper.mapWordToAllWordsDb(it)
                wordDao.insertAllWord(allWords)
            }
            return@withContext response

        } catch (e: Exception) {
            return@withContext emptyList()
        }

    }

    override suspend fun saveFavoriteWord(word: Word) {
        val wordDbFavorites = wordMapper.mapWordToWordDbFavorites(word)
        wordDao.insertFavoritesWord(wordDbFavorites)
    }

    override suspend fun deleteFavoriteWord(word: Word) {
        val wordDbModel = wordMapper.mapWordToWordDbFavorites(word)
        wordDao.deleteWord(wordDbModel)
    }

    override suspend fun isFavorite(word: Word): Boolean {
        val wordDbFavorite = wordDao.getWordById(word.id)
        return wordDbFavorite != null
    }

    override fun error(): Boolean {
        return error.value
    }
}