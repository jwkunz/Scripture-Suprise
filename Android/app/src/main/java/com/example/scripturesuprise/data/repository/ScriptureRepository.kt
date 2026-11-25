package com.example.scripturesuprise.data.repository

import com.example.scripturesuprise.data.local.dao.ScriptureDao
import com.example.scripturesuprise.data.local.datastore.FilterPreferences
import com.example.scripturesuprise.data.local.entity.ScriptureVerse
import com.example.scripturesuprise.data.remote.ScriptureImporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScriptureRepository @Inject constructor(
    private val dao: ScriptureDao,
    private val filterPrefs: FilterPreferences,
    private val importer: ScriptureImporter
) {
    val excludedVolumes = filterPrefs.excludedVolumes
    val excludedBooks = filterPrefs.excludedBooks
    val excludedChapters = filterPrefs.excludedChapters

    suspend fun getRandomVerse(): ScriptureVerse? {
        val excludedV = filterPrefs.excludedVolumes.first().toList()
        val excludedB = filterPrefs.excludedBooks.first().toList()
        val excludedC = filterPrefs.excludedChapters.first().toList()
        
        return dao.getRandomVerse(
            excludedVolumes = excludedV,
            hasExcludedVolumes = excludedV.isNotEmpty(),
            excludedBooks = excludedB,
            hasExcludedBooks = excludedB.isNotEmpty(),
            excludedChapters = excludedC,
            hasExcludedChapters = excludedC.isNotEmpty()
        )
    }

    suspend fun refreshScriptures(onProgress: (String) -> Unit): Result<Unit> {
        return importer.refreshDatabase(onProgress)
    }

    suspend fun toggleVolume(volume: String, exclude: Boolean) {
        filterPrefs.toggleVolume(volume, exclude)
    }

    suspend fun toggleBook(book: String, exclude: Boolean) {
        filterPrefs.toggleBook(book, exclude)
    }

    fun getVolumes(): Flow<List<String>> = dao.getVolumes()
    fun getBooksByVolume(volume: String): Flow<List<String>> = dao.getBooksByVolume(volume)
}
