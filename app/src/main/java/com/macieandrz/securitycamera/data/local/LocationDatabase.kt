package com.macieandrz.securitycamera.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.macieandrz.securitycamera.data.models.Location

@Database(entities = [Location::class], version = 2, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

}


object LocationDb {
    private var db: LocationDatabase? = null

    fun getInstance(context: Context): LocationDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context,
                LocationDatabase::class.java,
                "location_database"
            )
                .fallbackToDestructiveMigration().build()
        }
        return db!!
    }


}