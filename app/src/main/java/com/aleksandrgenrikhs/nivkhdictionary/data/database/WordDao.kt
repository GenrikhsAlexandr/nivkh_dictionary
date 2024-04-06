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
    suspend fun insertFavoriteWord(word: FavoriteWordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("SELECT * FROM words")
    fun getWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM word")
    fun getFavorites(): Flow<List<FavoriteWordEntity>>

    @Query("SELECT * FROM word WHERE id = :wordId")
    suspend fun getWordById(wordId: Int): FavoriteWordEntity?

    @Delete
    suspend fun deleteWord(word: FavoriteWordEntity)
}
