
package com.example.safisha.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.safisha.data.local.dao.SafishaDao
import com.example.safisha.data.local.entity.IssueReportEntity
import com.example.safisha.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        IssueReportEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SafishaDatabase : RoomDatabase() {

    abstract fun safishaDao(): SafishaDao

    companion object {

        @Volatile
        private var INSTANCE: SafishaDatabase? = null

        fun getDatabase(context: Context): SafishaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafishaDatabase::class.java,
                    "safisha_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
