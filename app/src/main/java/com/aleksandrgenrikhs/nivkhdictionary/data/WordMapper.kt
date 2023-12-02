package com.aleksandrgenrikhs.nivkhdictionary.data

import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordDbModel
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

    fun mapWordDbModelToWord(wordDbModel: WordDbModel): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap["nv"] = LocaleData(Locale("nv"), wordDbModel.nv)
        wordDbModel.ru?.let {
            localesMap["ru"] = LocaleData(Locale("ru"), it)
        }
        wordDbModel.en?.let {
            localesMap["en"] = LocaleData(Locale("en"), it)
        }
        return Word(id = wordDbModel.id, locales = localesMap)

    }

    fun mapWordToWordDbModel(word: Word) = WordDbModel(
        id = word.id,
        nv = word.locales["nv"]?.value ?: "",
        ru = word.locales["ru"]?.value ?: "",
        en = word.locales["en"]?.value ?: ""
    )
}