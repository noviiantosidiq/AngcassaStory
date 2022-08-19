package com.angcassa.stroyapp.config.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.angcassa.stroyapp.config.response.StoryforDatabase

@Database(
    entities = [StoryforDatabase::class, RemoteKey::class],
    version = 2,
    exportSchema = false
)

abstract class StoryDatabase : RoomDatabase(){
    abstract fun StoryDao(): StoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object{
        @Volatile
        private var INSTANSE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase{
            return INSTANSE ?: synchronized(this){
                INSTANSE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "Story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANSE = it }
            }
        }
    }
}