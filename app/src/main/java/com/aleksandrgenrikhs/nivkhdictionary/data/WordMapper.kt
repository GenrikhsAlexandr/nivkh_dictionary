package com.aleksandrgenrikhs.nivkhdictionary.data

import com.aleksandrgenrikhs.nivkhdictionary.data.database.AllWordsDb
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDbFavorites
import com.aleksandrgenrikhs.nivkhdictionary.domain.LocaleData
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import java.util.Locale
import javax.inject.Inject

class WordMapper @Inject constructor() {
    fun mapToWord(wordDto: WordDto): Word? {
        return Word(
            id = wordDto.id ?: return null,
            locales = buildMap {
                put(
                    "nv", LocaleData(
                        locale = Locale("nv"),
                        value = wordDto.nv ?: return null
                    )
                )
                wordDto.ru?.let {
                    put(
                        "ru", LocaleData(
                            locale = Locale("ru"),
                            value = it
                        )
                    )
                }
                wordDto.en?.let {
                    put(
                        "en", LocaleData(
                            locale = Locale.ENGLISH,
                            value = it
                        )
                    )
                }
            }
        )
    }

    fun mapWordDbFavoritesToWord(wordDbFavorites: WordDbFavorites): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap["nv"] = LocaleData(Locale("nv"), wordDbFavorites.nv)
        wordDbFavorites.ru?.let {
            localesMap["ru"] = LocaleData(Locale("ru"), it)
        }
        wordDbFavorites.en?.let {
            localesMap["en"] = LocaleData(Locale("en"), it)
        }
        return Word(id = wordDbFavorites.id, locales = localesMap)
    }

    fun mapWordToWordDbFavorites(word: Word) = WordDbFavorites(
        id = word.id,
        nv = word.locales["nv"]?.value ?: "",
        ru = word.locales["ru"]?.value ?: "",
        en = word.locales["en"]?.value ?: ""
    )

    fun mapAllWordsDbToWord(allWordsDb: AllWordsDb): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap["nv"] = LocaleData(Locale("nv"), allWordsDb.nv)
        allWordsDb.ru?.let {
            localesMap["ru"] = LocaleData(Locale("ru"), it)
        }
        allWordsDb.en?.let {
            localesMap["en"] = LocaleData(Locale("en"), it)
        }
        return Word(id = allWordsDb.id, locales = localesMap)
    }

    fun mapWordToAllWordsDb(word: Word) = AllWordsDb(
        id = word.id,
        nv = word.locales["nv"]?.value ?: "",
        ru = word.locales["ru"]?.value ?: "",
        en = word.locales["en"]?.value ?: ""
    )
}