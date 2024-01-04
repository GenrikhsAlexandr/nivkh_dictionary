package com.aleksandrgenrikhs.nivkhdictionary.domain

data class LocaleData(
    val locale: Language,
    val value: String,
    val audioPath: String? = null

)