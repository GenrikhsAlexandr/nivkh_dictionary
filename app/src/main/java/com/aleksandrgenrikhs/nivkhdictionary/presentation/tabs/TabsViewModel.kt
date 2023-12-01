package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandrgenrikhs.nivkhdictionary.domain.DictionaryInteractor
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class TabsViewModel @Inject constructor(
    private val interactor: DictionaryInteractor
) : ViewModel() {

    private val _words: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())

    val words: StateFlow<List<WordListItem>> = _words.map { words ->
        words.mapNotNull { word ->
            WordListItem(
                word = word,
                title = word.locales[currentLocale.value.language]?.value ?: return@mapNotNull null
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val currentLocale: MutableStateFlow<Locale> = MutableStateFlow(Locale("nv"))

    init {
        viewModelScope.launch {
            _words.value = interactor.getWords()
            print("_words.value = ${_words.value} ")
        }
    }
}