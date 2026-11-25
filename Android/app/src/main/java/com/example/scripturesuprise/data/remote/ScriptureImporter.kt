package com.example.scripturesuprise.data.remote

import com.example.scripturesuprise.data.local.dao.ScriptureDao
import com.example.scripturesuprise.data.local.entity.ScriptureVerse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface ScriptureImporter {
    suspend fun refreshDatabase(onProgress: (String) -> Unit): Result<Unit>
}

@Singleton
class ScriptureImporterImpl @Inject constructor(
    private val parser: ChurchWebsiteParser,
    private val dao: ScriptureDao
) : ScriptureImporter {

    private val volumes = listOf(
        VolumeInfo("Old Testament", "ot"),
        VolumeInfo("New Testament", "nt"),
        VolumeInfo("Book of Mormon", "bofm"),
        VolumeInfo("Doctrine and Covenants", "dc-testament"),
        VolumeInfo("Pearl of Great Price", "pgp")
    )

    override suspend fun refreshDatabase(onProgress: (String) -> Unit): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            var totalVersesImported = 0
            
            for (volume in volumes) {
                android.util.Log.i("ScriptureImporter", "Starting import for volume: ${volume.name}")
                onProgress("Discovering books in ${volume.name}...")
                
                val books = try { 
                    parser.fetchBooks(volume.slug) 
                } catch (e: Exception) { 
                    android.util.Log.e("ScriptureImporter", "Failed to fetch books for ${volume.name}: ${e.message}")
                    emptyList() 
                }
                
                android.util.Log.d("ScriptureImporter", "Found ${books.size} books in ${volume.name}")

                for (book in books) {
                    onProgress("Discovering chapters in ${book.name}...")
                    val chapters = try { 
                        parser.fetchChapters(volume.slug, book.slug) 
                    } catch (e: Exception) { 
                        android.util.Log.e("ScriptureImporter", "Failed to fetch chapters for ${book.name}: ${e.message}")
                        emptyList() 
                    }
                    
                    if (chapters.isEmpty()) {
                        android.util.Log.w("ScriptureImporter", "No chapters found for book: ${book.name} - skipping")
                        continue
                    }

                    val bookVerses = mutableListOf<ScriptureVerse>()
                    for (chapter in chapters) {
                        onProgress("Importing ${book.name} $chapter...")
                        try {
                            val verses = parser.parseChapter(volume.slug, book.slug, volume.name, book.name, chapter)
                            if (verses.isNotEmpty()) {
                                bookVerses.addAll(verses)
                                android.util.Log.v("ScriptureImporter", "Imported ${verses.size} verses from ${book.name} $chapter")
                            }
                            delay(50) 
                        } catch (e: Exception) {
                            android.util.Log.e("ScriptureImporter", "Error parsing ${book.name} $chapter: ${e.message}")
                        }
                    }
                    
                    if (bookVerses.isNotEmpty()) {
                        // Incremental save so the app is usable immediately
                        dao.insertVerses(bookVerses)
                        totalVersesImported += bookVerses.size
                        android.util.Log.i("ScriptureImporter", "Saved ${bookVerses.size} verses from ${book.name} to DB")
                    }
                }
            }

            if (totalVersesImported > 0) {
                android.util.Log.i("ScriptureImporter", "Import complete. Total: $totalVersesImported")
                Result.success(Unit)
            } else {
                android.util.Log.e("ScriptureImporter", "No verses found during the entire import process.")
                Result.failure(Exception("No verses found during import. Please check your internet connection and try again."))
            }
        } catch (e: Exception) {
            android.util.Log.e("ScriptureImporter", "Critical error during import: ${e.message}", e)
            Result.failure(e)
        }
    }

    private data class VolumeInfo(val name: String, val slug: String)
}
