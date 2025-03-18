package com.macieandrz.securitycamera.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.macieandrz.securitycamera.data.models.CrimeStatItem
import com.macieandrz.securitycamera.data.models.*
import com.macieandrz.securitycamera.data.models.CrimeStatLocation
import kotlinx.coroutines.flow.Flow


@Dao
interface CrimeStatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crimeStat: List<CrimeStatItem>)

    @Delete
    suspend fun delete(crimeStat: List<CrimeStatItem>)

    @Update
    suspend fun update(crimeStat: CrimeStatItem)


    @Query("SELECT * FROM crime_stat_table WHERE ABS(latitude - :latitude) < 0.01 AND ABS(longitude - :longitude) < 0.01 AND month = :date")
    fun getCategory(date: String, latitude: Double, longitude: Double): Flow<List<CrimeStatItem>?>

    @Query("DELETE FROM crime_stat_table")
    suspend fun dropDatabase()


}