package com.example.scripturesuprise.data.repository

import com.example.scripturesuprise.data.local.dao.ScriptureDao
import com.example.scripturesuprise.data.local.datastore.FilterPreferences
import com.example.scripturesuprise.data.local.entity.ScriptureVerse
import com.example.scripturesuprise.data.remote.ScriptureImporter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class ScriptureRepositoryTest {

    @Mock
    private lateinit var dao: ScriptureDao

    @Mock
    private lateinit var filterPrefs: FilterPreferences

    @Mock
    private lateinit var importer: ScriptureImporter

    private lateinit var repository: ScriptureRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ScriptureRepository(dao, filterPrefs, importer)
    }

    @Test
    fun `getRandomVerse should call dao with excluded filters`() = runTest {
        val excludedV = setOf("OT")
        val excludedB = setOf("Genesis")
        `when`(filterPrefs.excludedVolumes).thenReturn(flowOf(excludedV))
        `when`(filterPrefs.excludedBooks).thenReturn(flowOf(excludedB))
        `when`(filterPrefs.excludedChapters).thenReturn(flowOf(emptySet()))

        val expectedVerse = ScriptureVerse(1, "BofM", "1 Nephi", 1, 1, "I, Nephi...", "url")
        `when`(dao.getRandomVerse(any(), any(), any())).thenReturn(expectedVerse)

        val result = repository.getRandomVerse()

        assertEquals(expectedVerse, result)
    }
}
