package com.macieandrz.securitycamera.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.macieandrz.securitycamera.data.models.Location
import kotlinx.coroutines.flow.Flow


@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: List<Location>)

    @Delete
    suspend fun delete(location: List<Location>)

    @Update
    suspend fun update(location: Location)

    @Query("SELECT * FROM location_table WHERE address=:address")
    fun getLocation(address: String): Flow<Location?>

    @Query("DELETE FROM location_table")
    suspend fun dropDatabase()


}