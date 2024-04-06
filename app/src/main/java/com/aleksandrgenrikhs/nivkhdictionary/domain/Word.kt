package com.aleksandrgenrikhs.nivkhdictionary.domain

data class Word(
    val id: Int,
    val locales: Map<String, LocaleData>,
)