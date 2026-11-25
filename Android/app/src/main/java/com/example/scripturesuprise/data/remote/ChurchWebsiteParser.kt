package com.example.scripturesuprise.data.remote

import com.example.scripturesuprise.data.local.entity.ScriptureVerse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton

data class BookMetadata(val name: String, val slug: String)

@Singleton
class ChurchWebsiteParser @Inject constructor() {

    private val baseUrl = "https://www.churchofjesuschrist.org/study/scriptures"
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    private fun connect(url: String): Document {
        return Jsoup.connect(url)
            .userAgent(userAgent)
            .timeout(15000)
            .followRedirects(true)
            .get()
    }

    fun fetchBooks(volumeSlug: String): List<BookMetadata> {
        val url = "$baseUrl/$volumeSlug?lang=eng"
        val doc = connect(url)
        
        // Use a more specific selector for book links to avoid navigation/footer links
        val links = doc.select("a.list-tile, a.sectionTitle-_Dn99, a.content-tile")
        
        val excludedSlugs = setOf(
            "title-page", "bofm-title", "introduction", "three", "eight", "js", 
            "explanation", "illustrations", "pronunciation", "reference", "_contents",
            "introduction-to-the-doctrine-and-covenants", "testimony-of-the-twelve-apostles"
        )

        return links.mapNotNull { element ->
            val href = element.attr("href")
            val name = element.select("span, p.title").first()?.text() ?: element.text()
            
            if (href.contains("/scriptures/$volumeSlug/")) {
                var slug = href.substringAfter("/scriptures/$volumeSlug/").substringBefore("?")
                
                // For single-chapter books like Enos, the link might be /enos/1
                if (slug.endsWith("/1")) {
                    slug = slug.substringBeforeLast("/1")
                }
                
                if (slug.isNotEmpty() && !slug.contains("/") && !excludedSlugs.contains(slug)) {
                    BookMetadata(name.trim(), slug)
                } else null
            } else null
        }.distinctBy { it.slug }
    }

    fun fetchChapters(volumeSlug: String, bookSlug: String): List<Int> {
        val url = "$baseUrl/$volumeSlug/$bookSlug?lang=eng"
        val doc = try { connect(url) } catch (e: Exception) { return emptyList() }
        
        // Search for chapter links in the navigation list
        val links = doc.select("a.list-tile, a.chapter-tile, nav a")
        val chapters = links.mapNotNull { element ->
            val href = element.attr("href")
            if (href.contains("/scriptures/$volumeSlug/$bookSlug/")) {
                val chapterStr = href.substringAfter("/scriptures/$volumeSlug/$bookSlug/").substringBefore("?")
                chapterStr.toIntOrNull()
            } else null
        }.distinct().sorted()

        // If no chapter links found, check if this page itself contains verses (single-chapter book)
        return if (chapters.isEmpty()) {
            val hasVerses = doc.select("p.verse, p[id^=p], p[data-aid]").isNotEmpty()
            if (hasVerses) listOf(1) else emptyList()
        } else {
            chapters
        }
    }

    fun parseChapter(volumeSlug: String, bookSlug: String, volumeName: String, bookName: String, chapter: Int): List<ScriptureVerse> {
        val url = "$baseUrl/$volumeSlug/$bookSlug/$chapter?lang=eng"
        
        var doc: Document
        try {
            doc = connect(url)
        } catch (e: Exception) {
            if (chapter == 1) {
                // Fallback for single-chapter books that don't use the /1 suffix
                doc = connect("$baseUrl/$volumeSlug/$bookSlug?lang=eng")
            } else throw e
        }
        
        val verses = mutableListOf<ScriptureVerse>()

        // Remove study aids and navigation elements
        doc.select("sup.marker, .navigation, footer, header, .study-notes").remove()
        
        // Keep the text inside footnote links but remove the links themselves
        doc.select(".study-note-ref").unwrap()

        // Robust selector for verses
        val verseElements = doc.select("p.verse, p[id^=p], p[data-aid]")
        
        verseElements.forEach { element ->
            val id = element.attr("id")
            
            // 1. Try to find verse number in span.verse-number
            var verseNum = element.select(".verse-number").first()?.also { it.remove() }?.text()?.trim()?.toIntOrNull()
            
            // 2. Fallback to extracting from ID (e.g., "p1")
            if (verseNum == null || verseNum == 0) {
                verseNum = id.filter { it.isDigit() }.toIntOrNull()
            }
            
            val text = element.text().trim()
            
            // 3. Last resort: check if text starts with a number (some older sections might lack proper tags)
            if (verseNum == null || verseNum == 0) {
                val match = Regex("^(\\d+)\\s+").find(text)
                if (match != null) {
                    verseNum = match.groupValues[1].toIntOrNull()
                }
            }

            if (verseNum != null && verseNum > 0 && text.isNotEmpty()) {
                verses.add(
                    ScriptureVerse(
                        volume = volumeName,
                        book = bookName,
                        chapter = chapter,
                        verse = verseNum,
                        text = text,
                        churchUrl = url.substringBefore("?") + ".$verseNum?lang=eng"
                    )
                )
            }
        }
        
        android.util.Log.d("ChurchWebsiteParser", "Parsed ${verses.size} verses from $url")
        return verses
    }
}
