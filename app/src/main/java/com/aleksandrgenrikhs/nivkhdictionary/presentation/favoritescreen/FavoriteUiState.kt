package com.aleksandrgenrikhs.nivkhdictionary.presentation.favoritescreen

import androidx.annotation.StringRes
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word

data class FavoriteUiState(
    val state: FavoriteWordUiState,
)

sealed class FavoriteWordUiState {
    data class Words(
        val words: List<Word> = emptyList(),
        val isFavorite: Boolean = false,
        val selected: Word? = null,
        val audioButtonIsVisible: Boolean = false,
    ) : FavoriteWordUiState()

    data class Error(
        @StringRes val message: Int,
    ) : FavoriteWordUiState()

    data object Loading : FavoriteWordUiState()
    data object Idle : FavoriteWordUiState()
}
