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
    suspend fun insertFavoritesWord(word: WordDbFavorites)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWord(word: AllWordsDb)

    @Query("SELECT * FROM all_word")
    fun getWordsFromDb(): Flow<List<AllWordsDb>>

    @Query("SELECT * FROM word")
    fun getFavorites(): Flow<List<WordDbFavorites>>

    @Query("SELECT * FROM all_word")
    fun getCount(): Flow<List<WordDbFavorites>>

    @Query("SELECT * FROM word WHERE id = :wordId")
    suspend fun getWordById(wordId: String): WordDbFavorites?

    @Delete
    suspend fun deleteWord(word: WordDbFavorites)

    @Query("DELETE FROM all_word")
    suspend fun deleteAll()
}