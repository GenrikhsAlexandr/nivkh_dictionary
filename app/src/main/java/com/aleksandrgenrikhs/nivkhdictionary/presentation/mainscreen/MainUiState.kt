package com.aleksandrgenrikhs.nivkhdictionary.presentation.mainscreen


import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed class MainUiState {
    @Immutable
    data object Loading : MainUiState()

    @Immutable
    data class WordUiState(
        val filterWords: ImmutableList<Word>,
        val isFavorite: Boolean = false,
        val selected: Word? = null,
        val error: Error? = null,
        val isLoading: Boolean = true,
        val isSelected: Boolean = false,
        val audioButtonIsVisible: Boolean = false,
    ): MainUiState()

    data object Empty : MainUiState()

    @Immutable
    data class Error(@StringRes val message: Int) : MainUiState()

}
