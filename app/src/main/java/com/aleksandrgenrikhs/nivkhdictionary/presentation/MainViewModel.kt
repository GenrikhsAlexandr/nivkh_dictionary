package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import com.aleksandrgenrikhs.nivkhdictionary.utils.WordMediaPlayer
import kotlinx.coroutines.Job
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
    private val application: Application,
    private val player: WordMediaPlayer
) : ViewModel() {

    private lateinit var initPlayerJob: Job

    private val _isWordNotFound: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isWordNotFound: StateFlow<Boolean> = _isWordNotFound

    private val _isFavoriteWordNotFound: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFavoriteWordNotFound: StateFlow<Boolean> = _isFavoriteWordNotFound

    private val _isReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    private val _isSelected: MutableStateFlow<Word?> = MutableStateFlow(null)
    val isSelected: StateFlow<Word?> = _isSelected
    private val errorResponse: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val searchRequest: MutableStateFlow<String> = MutableStateFlow("")
    val isSearchViewVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProgressBarVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRvWordVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val toastMessage: MutableSharedFlow<Int> = MutableSharedFlow(
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
        words.filterByRequest(request = request)
    }.map { words ->
        _isWordNotFound.value = words.isEmpty() && !isProgressBarVisible.value
        words.mapToWordListItem()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val isErrorLayoutVisible: StateFlow<Boolean> = combine(
        _isReady,
        errorResponse
    ) { isReady, isErrorLayoutVisible ->
        isReady && !isErrorLayoutVisible
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            getWords()
        }
    }

    fun getWordsForStart() {
        viewModelScope.launch {
            when (val result = interactor.getWordForStartApp()) {
                is ResultState.Error -> {
                    if (!interactor.isNetWorkConnected()) {
                        toastMessage.tryEmit(R.string.error_message)
                    } else {
                        toastMessage.tryEmit(result.message)
                    }
                    errorResponse.tryEmit(true)
                }

                is ResultState.Success -> {
                    result.data
                    errorResponse.tryEmit(false)

                }
            }
            _isReady.tryEmit(true)
            isProgressBarVisible.tryEmit(false)

        }
    }

    fun refresh() {
        isProgressBarVisible.tryEmit(true)
        getWordsForStart()
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

    fun updateWords() {
        viewModelScope.launch {
            when (val result = interactor.updateWord()) {
                is ResultState.Error -> {
                    if (!interactor.isNetWorkConnected()) {
                        ResultState.Error(R.string.error_message)
                    } else
                        toastMessage.tryEmit(result.message)
                }

                is ResultState.Success -> {
                    toastMessage.tryEmit(R.string.update_words)
                }
            }
        }
    }

    fun onWordClicked(word: Word) {
        _isSelected.value = word
    }

    fun searchDestroy() {
        isSearchViewVisible.value = false
    }

    fun initPlayer() {
        initPlayerJob = viewModelScope.launch {
            if (!interactor.isNetWorkConnected()) {
                toastMessage.tryEmit(R.string.error_message)
            } else {
                val nvLocale = isSelected.value?.locales?.get(Language.NIVKH.code)
                val url = "${nvLocale?.audioPath}"
                val result = player.initPlayer(application, url)
                if (result == null) {
                    toastMessage.tryEmit(R.string.error_server)
                }
            }
        }
    }

    fun speakWord() {
        player.play()
    }

    fun destroyPlayer() {
        player.destroyPlayer()
        initPlayerJob.cancel()
    }
}