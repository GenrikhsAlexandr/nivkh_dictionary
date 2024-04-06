package com.aleksandrgenrikhs.nivkhdictionary.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val nv: String,
    val ru: String?,
    val en: String?,
)