package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: WordInteractor,
    private val application: Application,
) : ViewModel() {

    private val _words: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())
    val isIconClick: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite
    val toastMessage: MutableSharedFlow<String> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private lateinit var favoritesWord: Word

    private val currentLocale: MutableStateFlow<Locale> = MutableStateFlow(Locale(""))

    private val _query: MutableStateFlow<String> = MutableStateFlow("")

    val words: StateFlow<List<WordListItem>> = _words.map { words ->
        words
            .mapNotNull { word ->
                WordListItem(
                    word = word,
                    title = word.locales[currentLocale.value.language]?.value
                        ?: return@mapNotNull null
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    val filterWords: StateFlow<List<WordListItem>> = combine(
        _query,
        words
    ) { searchQuery, words ->
        if (searchQuery.isEmpty()) {
            words
        } else {
            words.filter {
                it.title.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())

    fun onSearchQuery(query: String) {
        _query.value = query
        println("onSearchQuery = ${_query.value}")
    }

    fun setLocale(locale: String) {
        viewModelScope.launch {
            currentLocale.value = Locale(locale)
        }
    }

    fun getAllWords() {
        viewModelScope.launch {
            try {
                _words.value = interactor.getWords()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFavoritesWords() {

        viewModelScope.launch {
            try {
                interactor.getFavoritesWords().collect {
                    _words.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun onFavoriteButtonClicked() {
        isIconClick.value = !isIconClick.value
        viewModelScope.launch {
            if (isIconClick.value) {
                saveFavoritesWord(favoritesWord)
            } else {
                deleteFavoritesWord(favoritesWord)
            }
        }
    }

    private suspend fun saveFavoritesWord(word: Word) {
        interactor.saveFavoriteWord(word)
        toastMessage.tryEmit(application.getString(R.string.save_word))
    }

    private suspend fun deleteFavoritesWord(word: Word) {
        interactor.deleteFavoriteWord(word)
        toastMessage.tryEmit(application.getString(R.string.delete_word))
    }

    fun setWord(word: Word) {
        favoritesWord = word
        viewModelScope.launch {
            _isFavorite.value = interactor.isFavorite(word)
        }
    }
}