package com.aleksandrgenrikhs.nivkhdictionary.data

import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class WordRepositoryImpl(
) : WordRepository {

    companion object {
        private const val BASE_URL = "http://bibl-nogl-dictionary.ru"
    }

    private val mapperWord: MapperWord = MapperWord()


    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(
            OkHttpClient.Builder()
                .apply {
                    addInterceptor(
                        HttpLoggingInterceptor().setLevel(
                            HttpLoggingInterceptor
                                .Level.BODY
                        )
                    )
                }
                .build()
        )
        .build()

    private val service: WordService = retrofit.create(WordService::class.java)

    override suspend fun getWords(): List<Word> {
        return service.getWords().mapNotNull { WordDto ->
            mapperWord.mapToWord(WordDto)
        }
    }

    override fun getWordsCurrentLocale(locale: String): Flow<List<Word>> {
        TODO("Not yet implemented")
    }

    override fun getFavoritesWords(): Flow<List<Word>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveFavoriteWord(word: Word): Word {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoriteWord(word: Word) {
        TODO("Not yet implemented")
    }


    override suspend fun updateWords(): List<Word> {
        TODO("Not yet implemented")
    }


}