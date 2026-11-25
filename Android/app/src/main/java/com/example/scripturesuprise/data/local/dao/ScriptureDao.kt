package com.example.scripturesuprise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scripturesuprise.data.local.entity.ScriptureVerse
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptureDao {
    @Query("SELECT * FROM scriptures")
    fun getAllVerses(): Flow<List<ScriptureVerse>>

    @Query("""
        SELECT * FROM scriptures 
        WHERE (:hasExcludedVolumes = 0 OR volume NOT IN (:excludedVolumes))
        AND (:hasExcludedBooks = 0 OR book NOT IN (:excludedBooks))
        AND (:hasExcludedChapters = 0 OR (book || ' ' || chapter) NOT IN (:excludedChapters))
        ORDER BY RANDOM() LIMIT 1
    """)
    suspend fun getRandomVerse(
        excludedVolumes: List<String>,
        hasExcludedVolumes: Boolean,
        excludedBooks: List<String>,
        hasExcludedBooks: Boolean,
        excludedChapters: List<String>,
        hasExcludedChapters: Boolean
    ): ScriptureVerse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<ScriptureVerse>)

    @Query("DELETE FROM scriptures")
    suspend fun deleteAll()

    @Query("SELECT DISTINCT volume FROM scriptures")
    fun getVolumes(): Flow<List<String>>

    @Query("SELECT DISTINCT book FROM scriptures WHERE volume = :volume")
    fun getBooksByVolume(volume: String): Flow<List<String>>

    @Query("SELECT DISTINCT chapter FROM scriptures WHERE book = :book")
    fun getChaptersByBook(book: String): Flow<List<Int>>
}
