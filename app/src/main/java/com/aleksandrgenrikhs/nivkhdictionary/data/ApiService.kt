package com.aleksandrgenrikhs.nivkhdictionary.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("data/nivkhwords.json")
    suspend fun getWords(): List<WordDto>

    @GET("data/nivkhaudio/{wordId}.mp3")
    suspend fun getAudioWord(
        @Path("wordId") wordId: Int
    ): Response<Unit>
}