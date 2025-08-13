package com.example.hulaba3.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hulaba3.utils.UriTypeConverter

@Database(entities = [Word::class, Topic::class], version = 3, exportSchema = false)
@TypeConverters(UriTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun topicDao(): TopicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 2 to 3 (adds lastReviewed & nextReviewTime to topics)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE topics ADD COLUMN lastReviewed INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE topics ADD COLUMN nextReviewTime INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hulaba3_database"
                )
                    .addMigrations(MIGRATION_2_3) // Apply the migration safely
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
