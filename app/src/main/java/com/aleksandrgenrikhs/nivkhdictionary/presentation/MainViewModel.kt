package com.aleksandrgenrikhs.nivkhdictionary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: WordInteractor,
) : ViewModel() {

    private val _searchRequest: MutableStateFlow<String> = MutableStateFlow("")

    private val _isFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)

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

    private val _showErrorPage: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showErrorPage = _showErrorPage.asStateFlow()


    private lateinit var favoritesWord: Word

    val favoritesWords: StateFlow<List<WordListItem>> = combine(
        _searchRequest,
        interactor.getFavoritesWords()
    ) { request, words ->
        words.filterByRequest(request = request)
    }.map { words ->

        words.mapToWordListItem()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val words: StateFlow<List<WordListItem>> = combine(
        _searchRequest,
        interactor.getWords()
    ) { request, words ->
        words.filterByRequest(request = request)
    }.map { words ->
        words.mapToWordListItem()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        getWordsForStart()
    }

    private fun getWordsForStart() {
        viewModelScope.launch {
            when (interactor.getWordStartApp()) {
                is ResultState.Success -> getWords()
                is ResultState.Error -> _showErrorPage.tryEmit(true)
            }
        }
    }

    private fun List<Word>.filterByRequest(request: String): List<Word> {
        return if (request.isEmpty()) {
            this
        } else {
            this.filter { word ->
                word.locales[Strings.NIVKH]?.value?.contains(
                    request,
                    ignoreCase = true
                ) ?: false
                        || word.locales[Strings.ENGLISH]?.value?.contains(
                    request,
                    ignoreCase = true
                ) ?: false
                        || word.locales[Strings.RUSSIAN]?.value?.contains(
                    request,
                    ignoreCase = true
                ) ?: false
            }
        }
    }

    private fun List<Word>.mapToWordListItem(): List<WordListItem> {
        return this.map { word ->
            WordListItem(
                word = word
            )
        }
    }

    private fun getWords() {
        viewModelScope.launch {
            isProgressBarVisible.value = true
            isRvWordVisible.value = false
            interactor.getWords()
            isProgressBarVisible.value = false
            isRvWordVisible.value = true
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

    fun onSearchQuery(query: String) {
        _searchRequest.value = query
    }

    fun onDestroy() {
        isSearchViewVisible.value = false
    }

    suspend fun updateWords(): ResultState<List<Word>> {
        return interactor.updateWord()
    }
}