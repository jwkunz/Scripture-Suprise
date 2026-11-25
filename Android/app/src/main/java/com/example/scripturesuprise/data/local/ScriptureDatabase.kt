package com.example.scripturesuprise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scripturesuprise.data.local.dao.ScriptureDao
import com.example.scripturesuprise.data.local.entity.ScriptureVerse

@Database(entities = [ScriptureVerse::class], version = 3, exportSchema = false)
abstract class ScriptureDatabase : RoomDatabase() {
    abstract fun scriptureDao(): ScriptureDao
}
