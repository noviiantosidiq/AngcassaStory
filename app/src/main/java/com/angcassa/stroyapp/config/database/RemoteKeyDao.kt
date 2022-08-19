package com.angcassa.stroyapp.config.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(RemoteKey: List<RemoteKey>)

    @Query("SELECT * FROM remote_key WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKey?

    @Query("DELETE FROM Story")
    suspend fun deleteRemoteKey()
}