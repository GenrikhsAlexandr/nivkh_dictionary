package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.data.SearchRepository
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem
import com.aleksandrgenrikhs.nivkhdictionary.utils.NetworkConnected
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: WordInteractor,
    private val searchRepository: SearchRepository,
    private val application: Application,
    private val networkConnected: NetworkConnected
) : ViewModel() {

    private val _allWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())

    private val _favoritesWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())
    private val _isFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _error: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error

    private val currentLocale: MutableStateFlow<Locale> = MutableStateFlow(Locale(""))
    val isIconClick: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFavoriteFragment: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val isWordDetail: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearchViewVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProgressBarVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRvWordVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite
    val toastMessage: MutableSharedFlow<Int> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private lateinit var favoritesWord: Word

    val words: StateFlow<List<WordListItem>> = _allWords.map { words ->
        words
            .mapNotNull { word ->
                WordListItem(
                    word = word,
                    title = word.locales[currentLocale.value.language]?.value
                        ?: return@mapNotNull null
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    val favoritesWords: StateFlow<List<WordListItem>> = _favoritesWords.map { words ->
        words
            .mapNotNull { word ->
                WordListItem(
                    word = word,
                    title = word.locales[currentLocale.value.language]?.value
                        ?: return@mapNotNull null
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    fun onSearchQuery(query: String) {
        searchRepository.setSearchRequest(query)
    }

    init {
        viewModelScope.launch {
            try {
                isProgressBarVisible.value = true
                isRvWordVisible.value = false
                interactor.getAndSaveWords()
                isProgressBarVisible.value = false
                isRvWordVisible.value = true
            } catch (e: Exception) {
                toastMessage.tryEmit(R.string.error_message)
            }

        }
        getWordBD()
        getWordsFilter()
    }

    private fun getWordBD() {
        viewModelScope.launch {
            isProgressBarVisible.value = true
            isRvWordVisible.value = false
            interactor.getWordsFromDb().collect {
                searchRepository.allWord.value = it
                isProgressBarVisible.value = false
                isRvWordVisible.value = true
            }
        }
    }

    private fun getWordsFilter() {
        viewModelScope.launch {
            searchRepository.filterWords.collect { filterWords ->
                _allWords.value = filterWords
            }
        }
    }

    fun getFavoritesWords() {
        viewModelScope.launch {
            isProgressBarVisible.value = true
            isRvWordVisible.value = false
            interactor.getFavoritesWords().collect {
                _favoritesWords.value = it
                isProgressBarVisible.value = false
                isRvWordVisible.value = true
            }
        }
    }

    fun getAndSaveWords() {
        viewModelScope.launch {
            if (!networkConnected.isNetworkConnected(application)) {
                toastMessage.tryEmit(R.string.error_message)
            } else {
                _error.value = false
                toastMessage.tryEmit(R.string.dialog_update_words_title)
                try {
                    interactor.getAndSaveWords()
                } catch (e: Exception) {
                    toastMessage.tryEmit(R.string.error_message)
                }
                toastMessage.tryEmit(R.string.update_words_title)
            }
        }
    }

    fun setLocale(locale: String) {
        viewModelScope.launch {
            currentLocale.value = Locale(locale)
            println(" setLocale= ${currentLocale.value}")
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
        toastMessage.tryEmit(R.string.save_word)
    }

    private suspend fun deleteFavoritesWord(word: Word) {
        interactor.deleteFavoriteWord(word)
        toastMessage.tryEmit(R.string.delete_word)
    }

    fun isFavoritesWord(word: Word) {
        favoritesWord = word
        viewModelScope.launch {
            _isFavorite.value = interactor.isFavorite(word)
        }
    }

    fun onDestroy() {
        searchRepository.allWord.value = emptyList()
        isSearchViewVisible.value = false
    }
}