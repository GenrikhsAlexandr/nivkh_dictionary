package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings
import kotlinx.coroutines.Dispatchers
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

class MainViewModel
@Inject constructor(
    private val interactor: WordInteractor,
    private val application: Application
) : ViewModel() {

    private var player: MediaPlayer? = null

    private val _searchRequest: MutableStateFlow<String> = MutableStateFlow("")

    private val _isSelected: MutableStateFlow<Word?> = MutableStateFlow(null)
    val isSelected: StateFlow<Word?> = _isSelected

    val isFavorite: StateFlow<Boolean> = isSelected.map { word ->
        word?.let {
            interactor.isFavorite(it)
        } ?: false
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)


    val isSearchViewVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProgressBarVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRvWordVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val toastMessage: MutableSharedFlow<Int> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _showErrorPage: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showErrorPage = _showErrorPage.asStateFlow()

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
        viewModelScope.launch {
            val word = isSelected.value ?: return@launch
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
        _searchRequest.value = query
    }

    fun onDestroy() {
        isSearchViewVisible.value = false
    }

    suspend fun updateWords(): ResultState<List<Word>> {
        return interactor.updateWord()
    }

    fun onWordClicked(word: Word) {
        _isSelected.value = word
    }

    fun wordDetailsDestroy() {
        _isSelected.value = null
    }

    private fun createPlayer(): MediaPlayer? {
        val nvLocale = isSelected.value?.locales?.get(Language.NIVKH.code) ?: return null
        val url = "${nvLocale.audioPath}"
        return MediaPlayer.create(application, Uri.parse(url))
    }

    fun play() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                createPlayer()?.start()
                println("играет звук")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("ошибка")

        }
    }
}
