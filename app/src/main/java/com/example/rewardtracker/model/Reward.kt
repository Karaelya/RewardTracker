package com.example.rewardtracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "partner")
data class Partner(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "guy_first")
    val guyFirst: String,
    @ColumnInfo(name = "guy_last")
    val guyLast: String,
    @ColumnInfo(name = "girl_first")
    val girlFirst: String,
    @ColumnInfo(name = "girl_last")
    val girlLast: String,
    @ColumnInfo(name = "active")
    val active: Boolean = true,
    @ColumnInfo(name = "active_goal")
    val activeGoal: Long = 0L
)

data class PartnerDetails(
    val id: Long,
    val partnerNames: String,
    val goalID: Long,
    val goalNm: String,
    val reward: String,
    val ptsNeeded: Int,
    val ptsEarned: Int,
    val goalActive: Boolean
)

data class PartnerNames(
    val guyFirst: String,
    val guyLast: String,
    val girlFirst: String,
    val girlLast: String,
    val active: Boolean,
    val activeGoal: Long
)

@Entity(tableName = "goal")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "goal")
    val goal: String,
    @ColumnInfo(name = "reward")
    val reward: String,
    @ColumnInfo(name = "points_needed")
    val pointsNeeded: Int,
    @ColumnInfo(name = "points_earned")
    val pointsEarned: Int = 0,
    @ColumnInfo(name = "active")
    val active: Boolean = true
)

data class GoalDetails(
    val goal: String,
    val reward: String,
    val pointsNeeded: Int,
    val pointsEarned: Int,
    val active: Boolean
)