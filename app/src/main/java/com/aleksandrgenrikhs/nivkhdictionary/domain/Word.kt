package com.aleksandrgenrikhs.nivkhdictionary.domain

data class Word(
    val id: String,
    val locales: Map<String, LocaleData>,
)
