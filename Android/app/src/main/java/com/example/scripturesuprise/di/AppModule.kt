package com.example.scripturesuprise.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.scripturesuprise.data.local.ScriptureDatabase
import com.example.scripturesuprise.data.local.dao.ScriptureDao
import com.example.scripturesuprise.data.remote.ScriptureImporter
import com.example.scripturesuprise.data.remote.ScriptureImporterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImporterModule {
    @Binds
    @Singleton
    abstract fun bindScriptureImporter(impl: ScriptureImporterImpl): ScriptureImporter
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScriptureDatabase {
        return Room.databaseBuilder(
            context,
            ScriptureDatabase::class.java,
            "scriptures_db"
        ).fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                // Initial famous verses for offline use
                db.execSQL("INSERT INTO scriptures (volume, book, chapter, verse, text, churchUrl) VALUES ('Old Testament', 'Genesis', 1, 1, 'In the beginning God created the heaven and the earth.', 'https://www.churchofjesuschrist.org/study/scriptures/ot/gen/1.1?lang=eng')")
                db.execSQL("INSERT INTO scriptures (volume, book, chapter, verse, text, churchUrl) VALUES ('New Testament', 'John', 3, 16, 'For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.', 'https://www.churchofjesuschrist.org/study/scriptures/nt/john/3.16?lang=eng')")
                db.execSQL("INSERT INTO scriptures (volume, book, chapter, verse, text, churchUrl) VALUES ('Book of Mormon', 'Moroni', 10, 4, 'And when ye shall receive these things, I would exhort you that ye would ask God, the Eternal Father, in the name of Christ, if these things are not true; and if ye shall ask with a sincere heart, with real intent, having faith in Christ, he will manifest the truth of it unto you, by the power of the Holy Ghost.', 'https://www.churchofjesuschrist.org/study/scriptures/bofm/moro/10.4?lang=eng')")
                db.execSQL("INSERT INTO scriptures (volume, book, chapter, verse, text, churchUrl) VALUES ('Doctrine and Covenants', 'Section', 1, 38, 'What I the Lord have spoken, I have spoken, and I excuse not myself; and though the heavens and the earth pass away, my word shall not pass away, but shall all be fulfilled, whether by mine own voice or by the voice of my servants, it is the same.', 'https://www.churchofjesuschrist.org/study/scriptures/dc-testament/dc/1.38?lang=eng')")
                db.execSQL("INSERT INTO scriptures (volume, book, chapter, verse, text, churchUrl) VALUES ('Pearl of Great Price', 'Moses', 1, 39, 'For behold, this is my work and my glory—to bring to pass the immortality and eternal life of man.', 'https://www.churchofjesuschrist.org/study/scriptures/pgp/moses/1.39?lang=eng')")
            }
        }).build()
    }

    @Provides
    fun provideScriptureDao(database: ScriptureDatabase): ScriptureDao {
        return database.scriptureDao()
    }
}
