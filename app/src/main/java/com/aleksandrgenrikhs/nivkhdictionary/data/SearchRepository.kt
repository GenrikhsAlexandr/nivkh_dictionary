package com.aleksandrgenrikhs.nivkhdictionary.data

import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings.ENGLISH
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings.NIVKH
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings.RUSSIAN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

object SearchRepository {

    private val _searchRequest: MutableStateFlow<String> = MutableStateFlow("")
    val allWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())
    val favoritesWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())

    val filteredWords: StateFlow<List<Word>> = combine(
        _searchRequest,
        allWords
    ) { searchQuery, allWords ->
        if (searchQuery.isEmpty()) {
            allWords
        } else {
            allWords.filter { word ->
                word.locales[NIVKH]?.value?.contains(
                    searchQuery,
                    ignoreCase = true
                ) ?: false
                        || word.locales[ENGLISH]?.value?.contains(
                    searchQuery,
                    ignoreCase = true
                ) ?: false
                        || word.locales[RUSSIAN]?.value?.contains(
                    searchQuery,
                    ignoreCase = true
                ) ?: false
            }
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())

    val filteredFavoritesWords: StateFlow<List<Word>> = combine(
        _searchRequest,
        favoritesWords
    ) { searchQuery, allWords ->
        if (searchQuery.isEmpty()) {
            allWords
        } else {
            allWords.filter { word ->
                word.locales[NIVKH]?.value?.contains(
                    searchQuery,
                    ignoreCase = true
                ) ?: false
                        || word.locales[ENGLISH]?.value?.contains(
                    searchQuery,
                    ignoreCase = true
                ) ?: false
                        || word.locales[RUSSIAN]?.value?.contains(
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