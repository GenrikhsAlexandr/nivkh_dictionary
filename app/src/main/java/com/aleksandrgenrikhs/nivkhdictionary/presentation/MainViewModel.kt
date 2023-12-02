package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordInteractor
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: WordInteractor,
    private val application: Application
) : ViewModel() {

    val isIconClick: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _isFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    val toastMessage: MutableSharedFlow<String> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private lateinit var word: Word

    suspend fun onFavoriteButtonClicked() {
        isIconClick.value = !isIconClick.value
        viewModelScope.launch {
            if (isIconClick.value) {
                saveFavoritesWord(word)
            } else {
                deleteFavoritesWord(word)
            }
        }
        println("isIconClick = ${isIconClick.value}")
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
        this.word = word
        viewModelScope.launch {
            _isFavorite.value = interactor.isFavorite(word)
        }
    }
}