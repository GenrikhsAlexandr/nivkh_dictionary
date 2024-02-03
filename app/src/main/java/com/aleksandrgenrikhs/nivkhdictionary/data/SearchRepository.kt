package com.aleksandrgenrikhs.nivkhdictionary.data

import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

object SearchRepository {
    private val _searchRequest: MutableStateFlow<String> = MutableStateFlow("")
    val allWord: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())
    val favoritesWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())

    val filterWords: StateFlow<List<Word>> = combine(
        _searchRequest,
        allWord
    ) { searchQuery, allWords ->
        if (searchQuery.isEmpty()) allWords
        else {
            allWords.filter { word ->
                word.locales["nv"]?.value?.contains(
                    searchQuery,
                    ignoreCase = true
                ) ?: false ||
                        word.locales["en"]?.value?.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ?: false ||
                        word.locales["ru"]?.value?.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ?: false
            }
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())

    val filterFavoritesWords: StateFlow<List<Word>> = combine(
        _searchRequest,
        favoritesWords
    ) { searchQuery, allWords ->
        if (searchQuery.isEmpty()) allWords
        else {
            allWords.filter { word ->
                word.locales["nv"]?.value?.contains(
                    searchQuery,
                    ignoreCase = true
                ) ?: false ||
                        word.locales["en"]?.value?.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ?: false ||
                        word.locales["ru"]?.value?.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ?: false
            }
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())

    fun setSearchRequest(request: String) {
        _searchRequest.value = request
    }
}