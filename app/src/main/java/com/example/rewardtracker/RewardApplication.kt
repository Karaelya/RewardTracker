package com.example.rewardtracker

import android.app.Application
import com.example.rewardtracker.data.RewardDatabase

class RewardApplication : Application() {
    val database: RewardDatabase by lazy { RewardDatabase.getDatabase(this) }

}