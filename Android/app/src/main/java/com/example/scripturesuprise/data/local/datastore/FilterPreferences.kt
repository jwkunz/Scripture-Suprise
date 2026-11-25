package com.example.scripturesuprise.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_prefs")

@Singleton
class FilterPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val EXCLUDED_VOLUMES = stringSetPreferencesKey("excluded_volumes")
    private val EXCLUDED_BOOKS = stringSetPreferencesKey("excluded_books")
    private val EXCLUDED_CHAPTERS = stringSetPreferencesKey("excluded_chapters")

    val excludedVolumes: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[EXCLUDED_VOLUMES] ?: emptySet()
    }

    val excludedBooks: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[EXCLUDED_BOOKS] ?: emptySet()
    }

    val excludedChapters: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[EXCLUDED_CHAPTERS] ?: emptySet()
    }

    suspend fun toggleVolume(volume: String, exclude: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[EXCLUDED_VOLUMES] ?: emptySet()
            prefs[EXCLUDED_VOLUMES] = if (exclude) current + volume else current - volume
        }
    }

    suspend fun toggleBook(book: String, exclude: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[EXCLUDED_BOOKS] ?: emptySet()
            prefs[EXCLUDED_BOOKS] = if (exclude) current + book else current - book
        }
    }

    suspend fun toggleChapter(chapterKey: String, exclude: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[EXCLUDED_CHAPTERS] ?: emptySet()
            prefs[EXCLUDED_CHAPTERS] = if (exclude) current + chapterKey else current - chapterKey
        }
    }
}
