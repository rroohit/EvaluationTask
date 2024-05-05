package com.roh.evaluationtask.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RemoteKeys")
data class RemoteKeys(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    @ColumnInfo(name = "keyName")
    val keyName: String = "",
    val nextKey: Int?,
)