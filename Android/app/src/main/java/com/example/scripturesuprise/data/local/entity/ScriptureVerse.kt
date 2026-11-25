package com.example.scripturesuprise.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "scriptures",
    primaryKeys = ["volume", "book", "chapter", "verse"]
)
data class ScriptureVerse(
    val volume: String,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val churchUrl: String
)
