package com.aleksandrgenrikhs.nivkhdictionary.domain

data class Word(
    val id: Int,
    val locales: Map<String, LocaleData>,
) {
    fun getTitle(locale: String): String {
        return locales[locale]?.value ?: "No title"
    }
}
