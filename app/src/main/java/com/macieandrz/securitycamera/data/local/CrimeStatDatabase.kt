package com.macieandrz.securitycamera.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.macieandrz.securitycamera.data.models.CrimeStatItem
import com.macieandrz.securitycamera.data.models.Location

@Database(entities = [CrimeStatItem::class], version = 3, exportSchema = false)
abstract class CrimeStatDatabase : RoomDatabase() {
    abstract fun crimeStatDao(): CrimeStatDao

}


object CrimeStatDb {
    private var db: CrimeStatDatabase? = null

    fun getInstance(context: Context): CrimeStatDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context,
                CrimeStatDatabase::class.java,
                "crime_stat_database"
            )
                .fallbackToDestructiveMigration().build()
        }
        return db!!
    }


}