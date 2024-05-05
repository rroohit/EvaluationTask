package com.roh.evaluationtask.di

import android.content.Context
import androidx.room.Room
import com.roh.evaluationtask.data.local.PostDao
import com.roh.evaluationtask.data.local.PostDatabase
import com.roh.evaluationtask.data.local.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providePostDataBase(
        @ApplicationContext context: Context
    ): PostDatabase = Room.databaseBuilder(
        context,
        PostDatabase::class.java,
        "PostDB"
    ).build()


    @Provides
    @Singleton
    fun providePostDao(
        database: PostDatabase
    ): PostDao = database.postDao()


    @Provides
    @Singleton
    fun provideRemoteKeysDao(
        database: PostDatabase
    ): RemoteKeysDao = database.remoteKeyDao()

}