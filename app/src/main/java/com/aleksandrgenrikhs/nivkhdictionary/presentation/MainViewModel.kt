package com.aleksandrgenrikhs.nivkhdictionary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.data.SearchRepository
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
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
) : ViewModel() {

    private val _allWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())
    private val _searchWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())
    private val _searchFavoritesWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())

    private val _favoritesWords: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())
    private val _isFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)

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

    val words: StateFlow<List<WordListItem>> = _searchWords.map { words ->
        words
            .mapNotNull { word ->
                WordListItem(
                    word = word,
                    title = word.locales[currentLocale.value.language]?.value
                        ?: return@mapNotNull null
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favoritesWords: StateFlow<List<WordListItem>> = _searchFavoritesWords.map { words ->
        words
            .mapNotNull { word ->
                WordListItem(
                    word = word,
                    title = word.locales[currentLocale.value.language]?.value
                        ?: return@mapNotNull null
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        getFavoritesWords()
        getWordFromSearchRepository()
        getFavoritesFromSearchRepository()
        getWords()
    }

    suspend fun getWordStartApp(): ResultState<List<Word>> {
        return interactor.getWordStartApp()
    }

    private fun getWords() {
        viewModelScope.launch {
            isProgressBarVisible.value = true
            isRvWordVisible.value = false
            interactor.getWords().collect {
                _allWords.value = it
                isProgressBarVisible.value = false
                isRvWordVisible.value = true
            }
        }
        viewModelScope.launch {
            _allWords.collect {
                searchRepository.allWords.value = it
            }
        }
    }

    private fun getFavoritesWords() {
        viewModelScope.launch {
            interactor.getFavoritesWords().collect {
                _favoritesWords.value = it
            }
        }
        viewModelScope.launch {
            _favoritesWords.collect {
                searchRepository.favoritesWords.value = it
            }
        }
    }

    private fun getWordFromSearchRepository() {
        viewModelScope.launch {
            searchRepository.filteredWords.collect {
                _searchWords.value = it
            }
        }
    }

    private fun getFavoritesFromSearchRepository() {
        viewModelScope.launch {
            searchRepository.filteredFavoritesWords.collect {
                _searchFavoritesWords.value = it
            }
        }
    }

    suspend fun updateWords(): ResultState<List<Word>> {
        return interactor.updateWord()
    }

    fun setLocale(locale: String) {
        viewModelScope.launch {
            currentLocale.value = Locale(locale)
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
        searchRepository.setSearchRequest(query)
    }

    fun onDestroy() {
        searchRepository.allWords.value = emptyList()
        isSearchViewVisible.value = false
    }
}