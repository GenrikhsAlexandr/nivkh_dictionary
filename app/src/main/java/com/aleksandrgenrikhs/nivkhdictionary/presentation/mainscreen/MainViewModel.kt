package com.aleksandrgenrikhs.nivkhdictionary.presentation.mainscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import com.aleksandrgenrikhs.nivkhdictionary.presentation.UIStateMapper
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val interactor: WordInteractor,
    private val mapper: UIStateMapper,
) : ViewModel() {

    private val searchRequest: MutableStateFlow<String> = MutableStateFlow("")

    private val _isSearchViewVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearchViewVisible: StateFlow<Boolean> = _isSearchViewVisible

    var uiState: MainUiState by mutableStateOf(MainUiState.Loading)
        private set

    init {
        println("getWords")
        combine(
            flow = searchRequest,
            flow2 = interactor.getWords(),
            transform = { request, words ->
                filterWords(words = words, request = request)
            }
        ).map { filterWords ->
            handleFilterWords(filterWords)
        }
            .launchIn(viewModelScope)
    }


    private fun handleFilterWords(word: List<Word>) {
            uiState = if (word.isEmpty()) {
                MainUiState.Empty
            } else {
                MainUiState.WordUiState(filterWords = word.toImmutableList())
            }
    }
//    val flowWords: StateFlow<List<Word>> = combine(
//        searchRequest,
//        interactor.getWords()
//    ) { request, words ->
//        println("getWords")
//        words.filterByRequest(request = request)
//    }.onEach { words ->
//        _viewState.update {
//            if (words.isEmpty()) {
//                it.copy(state = WordUiState.Idle)
//            } else {
//                it.copy(state = WordUiState.Words(words = words.mapToWordListItem()))
//            }
//        }
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

//
//    fun isFavorite() {
//        viewModelScope.launch {
//            val currentState = uiState.state as WordUiState.Words
//            if (currentState.selected == null) {
//                return@launch
//            }
//            val isFavorite = interactor.isFavorite(currentState.selected)
//            uiState = uiState.copy(
//                state = WordUiState.Words(
//                    selected = currentState.selected,
//                    isFavorite = isFavorite
//                )
//            )
//        }
//    }

        fun getWordsForStart() {
            println("getWordsForStart")
            viewModelScope.launch {
                when (val result = interactor.getWordForStartApp()) {
                    is ResultState.Error -> {
                        handleError(result)
                        return@launch
                    }

                    is ResultState.Success -> Unit
                }
            }
        }


        private fun filterWords(
            words: List<Word>,
            request: String,
        ): List<Word> {
            return if (request.isEmpty()) {
                words
            } else {
                words.filter { word ->
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
//
//        fun onClickFavoriteButton() {
//            viewModelScope.launch {
//                val currentState = uiState.state as WordUiState.Words
//                if (currentState.selected != null) {
//                    val selectedWord = currentState.selected
//                    if (interactor.isFavorite(selectedWord)) {
//                        deleteFavoritesWord(selectedWord)
//                    } else {
//                        saveFavoritesWord(selectedWord)
//                    }
//                }
//            }
//        }

        private suspend fun saveFavoritesWord(word: Word) {
            interactor.saveFavoriteWord(word)
        }

        private suspend fun deleteFavoritesWord(word: Word) {
            interactor.deleteFavoriteWord(word)
        }

        fun onSearchQuery(query: String) {
            searchRequest.value = query
        }

        fun updateWords() {
            viewModelScope.launch {
                when (val result = interactor.updateWord()) {
                    is ResultState.Error -> {
                        handleError(result)
                        return@launch
                    }

                    is ResultState.Success -> {
                        getWordsForStart()
                    }
                }
            }
        }

        fun onWordClicked(word: Word) {
            val currentState = uiState
            if (currentState is MainUiState.WordUiState) {

                uiState = currentState.copy(
                    selected = word,
                    isSelected = true
                )
            }
        }

        fun searchVisible(value: Boolean) {
            _isSearchViewVisible.value = value
        }

//        suspend fun initPlayer() {
//            viewModelScope.launch {
//                if (!interactor.isNetWorkConnected()) {
//
//                    uiState =
//                        uiState.copy(state = WordUiState.Error(R.string.аudio_is_not_available))
//                }
//                return@launch
//            }
//            val wordId = uiState.state as WordUiState.Words
//            if (wordId.selected != null) {
//                val isUrlExist = interactor.isUrlExist(wordId.selected.id)
//                val nvLocale = wordId.selected.locales[Language.NIVKH.code]
//                val url = "${nvLocale?.audioPath}"
//                interactor.initPlayer(url, isUrlExist)
//
//                uiState = uiState.copy(state = WordUiState.Words(audioButtonIsVisible = isUrlExist))
//            }
//        }

        fun onDismiss() {
            val currentState = uiState
            if (currentState is MainUiState.WordUiState) {
                uiState = currentState.copy(
                    selected = null,
                    isSelected = false
                )
            }
        }

        fun speakWord() {
            interactor.play()
        }

        private fun handleError(result: ResultState.Error) {
            val messageRes = if (!interactor.isNetWorkConnected()) {
                R.string.error_message
            } else {
                result.message
            }
            uiState = MainUiState.Error(
                message = messageRes,
            )
        }

        fun destroyPlayer() {
            interactor.destroyPlayer()
        }

        override fun onCleared() {
            super.onCleared()
            searchVisible(false)
        }
    }
