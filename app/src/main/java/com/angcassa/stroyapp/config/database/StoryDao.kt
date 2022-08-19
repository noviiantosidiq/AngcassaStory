package com.angcassa.stroyapp.config.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.angcassa.stroyapp.config.response.StoryforDatabase

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(Story: List<StoryforDatabase>)

    @Query("SELECT * FROM Story")
    fun getAllStory(): PagingSource<Int, StoryforDatabase>

    @Query("DELETE FROM Story")
    suspend fun deleteAll()

    @Query("SELECT * FROM Story")
    fun getWStory(): List<StoryforDatabase>

}