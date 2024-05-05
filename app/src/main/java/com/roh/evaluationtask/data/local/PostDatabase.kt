package com.roh.evaluationtask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PostEntity::class, RemoteKeys::class],
    version = 1,
    autoMigrations = [] // to handle schema change
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun remoteKeyDao(): RemoteKeysDao
}