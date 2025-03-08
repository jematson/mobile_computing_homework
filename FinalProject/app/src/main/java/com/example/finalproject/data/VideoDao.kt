package com.example.finalproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM video")
    fun getAll(): Flow<List<Video>>

    @Query("SELECT * FROM video WHERE uid IN (:vidIds)")
    suspend fun loadAllByIds(vidIds: IntArray): List<Video>

    @Query("SELECT * FROM video WHERE title LIKE :title")
    suspend fun findByName(title: String): Video

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg videos: Video)

    @Delete
    suspend fun delete(location: Video)
}