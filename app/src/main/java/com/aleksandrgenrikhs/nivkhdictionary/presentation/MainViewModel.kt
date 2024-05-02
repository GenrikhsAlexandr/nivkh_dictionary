package com.aleksandrgenrikhs.nivkhdictionary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
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
import javax.inject.Inject

class MainViewModel
@Inject constructor(
    private val interactor: WordInteractor,
) : ViewModel() {

    private val _isWordNotFound: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isWordNotFound: StateFlow<Boolean> = _isWordNotFound

    private val _isFavoriteWordNotFound: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFavoriteWordNotFound: StateFlow<Boolean> = _isFavoriteWordNotFound

    private val _isSelected: MutableStateFlow<Word?> = MutableStateFlow(null)
    val isSelected: StateFlow<Word?> = _isSelected

    private val searchRequest: MutableStateFlow<String> = MutableStateFlow("")
    val isSearchViewVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProgressBarVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRvWordVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isClickable: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val toastMessage: MutableSharedFlow<Int> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val toastMessageError: MutableSharedFlow<ResultState.Error> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val isFavorite: StateFlow<Boolean> = isSelected.map { word ->
        word?.let {
            interactor.isFavorite(it)
        } ?: false
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val favoritesWords: StateFlow<List<WordListItem>> = combine(
        searchRequest,
        interactor.getFavoritesWords()
    ) { request, words ->
        words.filterByRequest(request = request)
    }.map { words ->
        _isFavoriteWordNotFound.value = words.isEmpty()
        words.mapToWordListItem()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val words: StateFlow<List<WordListItem>> = combine(
        searchRequest,
        interactor.getWords()
    ) { request, words ->
        println("interactor.getWords() = ${interactor.getWords()}")
        words.filterByRequest(request = request)
    }.map { words ->
        _isWordNotFound.value = words.isEmpty() && !isProgressBarVisible.value
        words.mapToWordListItem()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun getWordsForStart(): ResultState<List<Word>> {
        isProgressBarVisible.value = true
        return interactor.getWordForStartApp()
    }

    private fun List<Word>.filterByRequest(request: String): List<Word> {
        return if (request.isEmpty()) {
            this
        } else {
            this.filter { word ->
                word.locales[Language.NIVKH.code]?.value?.contains(
                    request,
                    ignoreCase = true
                ) ?: false
                        || word.locales[Language.RUSSIAN.code]?.value?.contains(
                    request,
                    ignoreCase = true
                ) ?: false
                        || word.locales[Language.ENGLISH.code]?.value?.contains(
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

    init {
        viewModelScope.launch {
            getWords()
        }
    }

    fun getWords() {
        viewModelScope.launch {
            isProgressBarVisible.value = true
            isRvWordVisible.value = false
            interactor.getWords()
            isProgressBarVisible.value = false
            isRvWordVisible.value = true
        }
    }

    suspend fun onFavoriteButtonClicked() {
        val word = isSelected.value
        if (word != null) {
            if (isFavorite.value) {
                deleteFavoritesWord(word)
            } else {
                saveFavoritesWord(word)
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

    fun onSearchQuery(query: String) {
        searchRequest.value = query
    }

    suspend fun updateWords(): ResultState<List<Word>> {
        return interactor.updateWord()
    }

    fun onWordClicked(word: Word) {
        _isSelected.value = word
    }

    fun soundWord() {
        viewModelScope.launch(Dispatchers.IO) {
            isClickable.value = false
            val nvLocale = isSelected.value?.locales?.get(Language.NIVKH.code)
            val url = "${nvLocale?.audioPath}"
            when (val sound = interactor.wordSounds(url)) {
                is ResultState.Error -> {
                    interactor.playerDestroy()
                    toastMessageError.tryEmit(ResultState.Error(sound.message))
                }

                is ResultState.Success -> {
                    sound.data?.start()
                }
            }
            isClickable.value = true
        }
    }

    fun wordDetailsDestroy() {
        _isSelected.value = null
        interactor.playerDestroy()
    }

    fun searchDestroy() {
        isSearchViewVisible.value = false
    }
}