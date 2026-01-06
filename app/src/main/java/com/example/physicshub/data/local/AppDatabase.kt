package com.example.physicshub.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RecentlyViewedEntity::class,
        EventEntity::class
    ],
    version = 2,  // Tăng version lên 2
    exportSchema = false
)
@TypeConverters(EventConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recentlyViewedDao(): RecentlyViewedDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "physics_hub_database"
                )
                    .fallbackToDestructiveMigration() // Để test, production nên dùng migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}