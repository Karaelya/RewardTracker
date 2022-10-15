package com.example.rewardtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rewardtracker.model.Goal
import com.example.rewardtracker.model.Partner


@Database(entities = [Partner::class, Goal::class], version = 1, exportSchema = false)
abstract class RewardDatabase : RoomDatabase()  {
    abstract fun RewardDao(): RewardDao

    companion object {

        @Volatile
        private var INSTANCE: RewardDatabase? = null

        fun getDatabase(context: Context): RewardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RewardDatabase::class.java,
                    "reward_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}