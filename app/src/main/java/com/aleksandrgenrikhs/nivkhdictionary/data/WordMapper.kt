package com.aleksandrgenrikhs.nivkhdictionary.data

import com.aleksandrgenrikhs.nivkhdictionary.data.WordRepositoryImpl.Companion.BASE_URL
import com.aleksandrgenrikhs.nivkhdictionary.data.database.FavoriteWordEntity
import com.aleksandrgenrikhs.nivkhdictionary.data.database.WordEntity
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.LocaleData
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings.ENGLISH
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings.NIVKH
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings.RUSSIAN
import javax.inject.Inject

const val HOST_URL = "${BASE_URL}/data"

class WordMapper @Inject constructor() {

    fun mapToWord(wordDto: WordDto): Word? {
        return Word(
            id = wordDto.id ?: return null,
            locales = buildMap {
                put(
                    NIVKH, LocaleData(
                        locale = Language.NIVKH,
                        value = wordDto.nv ?: return null
                    )
                )
                wordDto.ru?.let {
                    put(
                        RUSSIAN, LocaleData(
                            locale = Language.RUSSIAN,
                            value = it
                        )
                    )
                }
                wordDto.en?.let {
                    put(
                        ENGLISH, LocaleData(
                            locale = Language.ENGLISH,
                            value = it
                        )
                    )
                }
            }
        )
    }

    fun mapFavoriteWordEntityToWord(favoriteWord: FavoriteWordEntity): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap[NIVKH] = LocaleData(
            Language.NIVKH, favoriteWord.nv, "$HOST_URL/nivkhaudio/${favoriteWord.id}.mp3"
        )
        favoriteWord.ru?.let {
            localesMap[RUSSIAN] = LocaleData(Language.RUSSIAN, it)
        }
        favoriteWord.en?.let {
            localesMap[ENGLISH] = LocaleData(Language.ENGLISH, it)
        }
        return Word(id = favoriteWord.id, locales = localesMap)
    }

    fun mapWordToFavoriteWordEntity(word: Word) = FavoriteWordEntity(
        id = word.id,
        nv = word.locales[Language.NIVKH.code]?.value ?: "",
        ru = word.locales[Language.RUSSIAN.code]?.value ?: "",
        en = word.locales[Language.ENGLISH.code]?.value ?: "",
    )

    fun mapWordEntityToWord(wordEntity: WordEntity): Word {
        val localesMap = mutableMapOf<String, LocaleData>()
        localesMap[NIVKH] = LocaleData(
            Language.NIVKH, wordEntity.nv,
            "$HOST_URL/nivkhaudio/${wordEntity.id}.mp3"
        )
        wordEntity.ru?.let {
            localesMap[RUSSIAN] = LocaleData(Language.RUSSIAN, it)
        }
        wordEntity.en?.let {
            localesMap[ENGLISH] = LocaleData(Language.ENGLISH, it)
        }
        return Word(id = wordEntity.id, locales = localesMap)
    }

    fun mapWordToAllWordsDb(word: Word) = WordEntity(
        id = word.id,
        nv = word.locales[Language.NIVKH.code]?.value ?: "",
        ru = word.locales[Language.RUSSIAN.code]?.value ?: "",
        en = word.locales[Language.ENGLISH.code]?.value ?: "",
    )
}