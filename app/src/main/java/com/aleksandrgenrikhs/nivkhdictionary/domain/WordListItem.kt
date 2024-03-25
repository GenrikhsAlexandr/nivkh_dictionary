package com.aleksandrgenrikhs.nivkhdictionary.domain

import com.aleksandrgenrikhs.nivkhdictionary.R

data class WordListItem(
    val word: Word,
    val viewType: Int = R.layout.list_item_word
) {
    fun getTitle(locale: String): String = word.locales[locale]?.value ?: ""

}