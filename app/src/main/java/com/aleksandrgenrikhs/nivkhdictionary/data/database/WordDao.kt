package com.aleksandrgenrikhs.nivkhdictionary.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordDbModel)

    @Query("SELECT * FROM word")
    fun getWordsFromDb(): Flow<List<WordDbModel>>

    @Query("SELECT * FROM word WHERE id = :wordId")
    suspend fun getWordById(wordId: String): WordDbModel?

    @Delete
    suspend fun deleteWord(word: WordDbModel)
}