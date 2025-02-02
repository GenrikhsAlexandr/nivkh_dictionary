//package com.aleksandrgenrikhs.nivkhdictionary.presentation.favoritescreen
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
//import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
//import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
//import com.aleksandrgenrikhs.nivkhdictionary.presentation.mainscreen.WordUiState
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class FavoriteViewModel
//@Inject constructor(
//    private val interactor: WordInteractor,
//) : ViewModel() {
//
//    private val searchRequest: MutableStateFlow<String> = MutableStateFlow("")
//
//    private val _isSearchViewVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
//    val isSearchViewVisible: StateFlow<Boolean> = _isSearchViewVisible
//
//    private val _viewState = MutableStateFlow(FavoriteUiState(state = FavoriteWordUiState.Idle))
//    val viewState: StateFlow<FavoriteUiState> = _viewState
//
//    fun getFavoritesWords(): StateFlow<List<Word>> = combine(
//        searchRequest,
//        interactor.getFavoritesWords()
//    ) { request, words ->
//        words.filterByRequest(request = request)
//    }.onEach { words ->
//        _viewState.update {
//            if (words.isEmpty()) {
//                it.copy(state = FavoriteWordUiState.Idle)
//            } else {
//                it.copy(state = FavoriteWordUiState.Words(words = words.mapToWordListItem()))
//            }
//        }
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
//
//    fun isFavorite() {
//        viewModelScope.launch {
//            val currentState = viewState.value.state as WordUiState.Words
//
//            if (currentState.selected == null) {
//                return@launch
//            }
//            val isFavorite = interactor.isFavorite(currentState.selected)
//            _viewState.update {
//                it.copy(
//                    state = FavoriteWordUiState.Words(
//                        words = currentState.words,
//                        selected = currentState.selected,
//                        isFavorite = isFavorite
//                    )
//                )
//            }
//        }
//    }
//
//    private fun List<Word>.filterByRequest(request: String): List<Word> {
//        return if (request.isEmpty()) {
//            this
//        } else {
//            this.filter { word ->
//                word.locales[Language.NIVKH.code]?.value?.contains(
//                    request,
//                    ignoreCase = true
//                ) ?: false
//                        || word.locales[Language.RUSSIAN.code]?.value?.contains(
//                    request,
//                    ignoreCase = true
//                ) ?: false
//                        || word.locales[Language.ENGLISH.code]?.value?.contains(
//                    request,
//                    ignoreCase = true
//                ) ?: false
//            }
//        }
//    }
//
//    private fun List<Word>.mapToWordListItem(): List<Word> {
//        return this.map { word ->
//            Word(
//                id = word.id,
//                locales = word.locales,
//            )
//        }
//    }
//
//    suspend fun onClickFavoriteButton() {
//        val currentState = viewState.value.state
//        if (currentState is FavoriteWordUiState.Words && currentState.selected != null) {
//            val selectedWord = currentState.selected
//            if (interactor.isFavorite(selectedWord)) {
//                deleteFavoritesWord(selectedWord)
//            } else {
//                saveFavoritesWord(selectedWord)
//            }
//        }
//    }
//
//    private suspend fun saveFavoritesWord(word: Word) {
//        interactor.saveFavoriteWord(word)
//    }
//
//    private suspend fun deleteFavoritesWord(word: Word) {
//        interactor.deleteFavoriteWord(word)
//    }
//
//    fun onSearchQuery(query: String) {
//        searchRequest.value = query
//    }
//
//    fun onWordClicked(word: Word) {
//        val currentState = _viewState.value.state
//        if (currentState is FavoriteWordUiState.Words) {
//            _viewState.update {
//                it.copy(
//                    state = FavoriteWordUiState.Words(
//                        words = currentState.words,
//                        selected = word
//                    )
//                )
//            }
//        }
//    }
//
//    fun searchVisible(value: Boolean) {
//        _isSearchViewVisible.value = value
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        searchVisible(false)
//    }
//}
