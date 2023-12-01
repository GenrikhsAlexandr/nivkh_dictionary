package com.aleksandrgenrikhs.nivkhdictionary.domain

import com.ncbs.dictionary.domain.LocaleData

data class Word(
    val id: String,
    val locales: Map<String, LocaleData>,
)
