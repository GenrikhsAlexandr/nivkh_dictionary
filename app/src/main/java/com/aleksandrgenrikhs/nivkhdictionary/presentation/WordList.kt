package com.aleksandrgenrikhs.nivkhdictionary.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aleksandrgenrikhs.nivkhalphabetcompose.presentation.ui.theme.ic_launcher_background
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.presentation.mainscreen.MainUiState
import com.aleksandrgenrikhs.nivkhdictionary.presentation.mainscreen.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun WordsList(
    locale: String,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState  = viewModel.uiState

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        println("uistate = ${uiState}")
        when(uiState) {
           is MainUiState.Loading  -> {
                CircularProgressIndicator(
                    modifier = Modifier.wrapContentSize(),
                    color = ic_launcher_background
                )
            }

          is MainUiState.Error -> {
                Text(
                    text = stringResource(uiState.message),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

           is MainUiState.Empty -> {
                Text(
                    text = stringResource(R.string.nothingFound),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            is MainUiState.WordUiState -> {
                LazyColumn {
                    items(uiState.filterWords) { word ->
                        WordItem(
                            word = word,
                            locale = locale,
                            onClick = {
                                viewModel.onWordClicked(it)
                            })
                    }
                }
                if (uiState.selected != null) {
                    BottomSheetContent(
                        selectedWord = uiState.selected,
                        onDismiss = viewModel::onDismiss,
                        showBottomSheet = uiState.isSelected,
                    )
                }
            }
        }
    }
}

@Composable
fun WordItem(
    word: Word,
    locale: String,
    onClick: (Word) -> Unit,
) {
    Text(
        text = word.getTitle(locale),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick(word) },

        style = MaterialTheme.typography.titleMedium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    onDismiss: () -> Unit,
    showBottomSheet: Boolean,
    selectedWord: Word?,
) {
    println("Rendering BottomSheetContent: showBottomSheet = $showBottomSheet, selectedWord = $selectedWord")

    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()

    if (showBottomSheet || sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onDismiss()
                    }
                }
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                println("currentState = $selectedWord ")

                Text(
                    text = selectedWord?.getTitle(Language.NIVKH.code)
                        ?: stringResource(R.string.no_data),
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = selectedWord?.getTitle(Language.RUSSIAN.code)
                        ?: stringResource(R.string.no_data),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = selectedWord?.getTitle(Language.ENGLISH.code)
                        ?: stringResource(R.string.no_data),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

