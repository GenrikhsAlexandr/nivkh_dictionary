package com.aleksandrgenrikhs.nivkhdictionary.data

import com.aleksandrgenrikhs.nivkhdictionary.data.database.AllWordsDb
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDbFavorites
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.LocaleData
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import javax.inject.Inject

const val HOST_URL = "http://bibl-nogl-dictionary.ru/data"

class WordMapper @Inject constructor() {
    fun mapToWord(wordDto: WordDto): Word? {
        return Word(
            id = wordDto.id ?: return null,
            locales = buildMap {
                put(
                    "nv", LocaleData(
                        locale = Language.NIVKH,
                        value = wordDto.nv ?: return null
                    )
                )
                wordDto.ru?.let {
                    put(
                        "ru", LocaleData(
                            locale = Language.RUSSIAN,
                            value = it
                        )
                    )
                }
                wordDto.en?.let {
                    put(
                        "en", LocaleData(
                            locale = Language.ENGLISH,
                            value = it
                        )
                    )
                }
            }
        )
    }

    fun mapWordDbFavoritesToWord(wordDbFavorites: WordDbFavorites): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap["nv"] = LocaleData(
            Language.NIVKH, wordDbFavorites.nv, "$HOST_URL/audio/${wordDbFavorites.id}.mp3"

        )
        wordDbFavorites.ru?.let {
            localesMap["ru"] = LocaleData(Language.RUSSIAN, it)
        }
        wordDbFavorites.en?.let {
            localesMap["en"] = LocaleData(Language.ENGLISH, it)
        }
        return Word(id = wordDbFavorites.id, locales = localesMap)
    }

    fun mapWordToWordDbFavorites(word: Word) = WordDbFavorites(
        id = word.id,
        nv = word.locales["nv"]?.value ?: "",
        ru = word.locales["ru"]?.value ?: "",
        en = word.locales["en"]?.value ?: "",
    )

    fun mapAllWordsDbToWord(allWordsDb: AllWordsDb): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap["nv"] = LocaleData(
            Language.NIVKH, allWordsDb.nv,
            "$HOST_URL/audio/${allWordsDb.id}.mp3"
        )
        allWordsDb.ru?.let {
            localesMap["ru"] = LocaleData(Language.RUSSIAN, it)
        }
        allWordsDb.en?.let {
            localesMap["en"] = LocaleData(Language.ENGLISH, it)
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