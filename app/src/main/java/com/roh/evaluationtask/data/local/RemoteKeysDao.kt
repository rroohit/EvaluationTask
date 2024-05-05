package com.roh.evaluationtask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(remoteKey: RemoteKeys)

    @Query("Select * From RemoteKeys Where id = :id")
    suspend fun getRemoteKey(id: Int = 1): RemoteKeys?

    @Query("Delete From RemoteKeys")
    suspend fun deleteRemoteKeys()

}