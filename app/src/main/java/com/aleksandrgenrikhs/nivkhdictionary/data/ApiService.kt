package com.aleksandrgenrikhs.nivkhdictionary.data

import retrofit2.http.GET

interface ApiService {

    @GET("data/nivkhwords.json")
    suspend fun getWords(): List<WordDto>
}