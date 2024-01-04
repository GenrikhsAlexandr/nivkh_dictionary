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
        if (wordDto.id.isNullOrBlank() || wordDto.nv.isNullOrBlank() || wordDto.ru.isNullOrBlank() || wordDto.en.isNullOrBlank()) return null
        return Word(
            id = wordDto.id.toString(),
            locales = mapOf(
                Language.NIVKH.code to LocaleData(
                    Language.NIVKH,
                    wordDto.nv,
                    "$HOST_URL/audio/${wordDto.id}.mp3"
                ),
                Language.RUSSIAN.code to LocaleData(Language.RUSSIAN, wordDto.ru),
                Language.ENGLISH.code to LocaleData(Language.ENGLISH, wordDto.en),
            )
        )
    }

    fun mapWordDbFavoritesToWord(wordDbFavorites: WordDbFavorites): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap["nv"] = LocaleData(Language.NIVKH, wordDbFavorites.nv)
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
        en = word.locales["en"]?.value ?: ""
    )

    fun mapAllWordsDbToWord(allWordsDb: AllWordsDb): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap["nv"] = LocaleData(Language.NIVKH, allWordsDb.nv)
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