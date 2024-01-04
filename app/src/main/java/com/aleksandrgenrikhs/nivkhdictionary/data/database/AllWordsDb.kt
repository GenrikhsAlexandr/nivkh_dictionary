package com.aleksandrgenrikhs.nivkhdictionary.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "all_word")
data class AllWordsDb(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val nv: String,
    val ru: String?,
    val en: String?,
)