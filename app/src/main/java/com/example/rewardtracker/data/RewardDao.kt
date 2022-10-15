package com.example.rewardtracker.data


import androidx.room.*
import com.example.rewardtracker.model.*

import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {

    @Insert(entity = Partner::class, onConflict = OnConflictStrategy.REPLACE)
    fun addPartner(Partner: PartnerNames): Long

    @Insert(entity = Goal::class, onConflict = OnConflictStrategy.REPLACE)
    fun addGoal(getGoalDetails: GoalDetails): Long

    @Query(
        """
            UPDATE partner
            SET guy_first=:guyFirst,
                guy_last=:guyLast,
                girl_first=:girlFirst,
                girl_last=:girlLast
            WHERE id=:id
        """
    )
    fun updatePartner(
        id: Long,
        guyFirst: String,
        guyLast: String,
        girlFirst: String,
        girlLast: String
    )

    @Query(
        """
            UPDATE goal
            SET goal=:goal,
                reward=:reward,
                points_needed=:pointsNeeded
            where id = (SELECT goal
                FROM Partner
                WHERE id=:partnerID)
        """
    )
    fun updateGoal(
        partnerID: Long,
        goal: String,
        reward: String,
        pointsNeeded: Int
    )

    @Query(
        """
            UPDATE goal
            SET points_earned=:ptsEarned
            WHERE id = (SELECT active_goal
                FROM partner
                WHERE id=:partnerID)
        """
    )
    fun updatePts(ptsEarned: Int, partnerID: Long)

    @Query("""
        SELECT active_goal
        FROM partner
        WHERE id =:id
    """)
    fun getGoal(id: Long): Boolean

    @Query(
        """
            SELECT g.active
            FROM goal g
            JOIN partner p
            WHERE p.id =:id
                AND p.active_goal = g.id
            
        """
    )
    fun checkGoal(id: Long): Boolean

    @Query(
        """
            UPDATE partner
            SET active_goal=:goalID
            WHERE id =:partnerID
        """
    )
    fun changeGoal(
        goalID: Long,
        partnerID: Long
    )

    @Query(
        """
            UPDATE partner
            set active = 0
            WHERE id=:id
        """
    )
    fun deletePartner(id: Long)

    @Query(
        """
        SELECT
            p.id,
            p.guy_first || ' ' || p.guy_last || ' & ' || p.girl_first || ' ' || 
            p.girl_last AS partnerNames,
            p.active_goal AS goalID,
            g.goal AS goalNm,
            g.reward AS reward,
            g.points_needed AS ptsNeeded,
            g.points_earned AS ptsEarned,
            g.active AS goalActive
        FROM partner p
        JOIN goal g
            ON p.active_goal = g.id
        WHERE 
            p.active
        ORDER BY guy_last ASC,
            guy_first ASC,
            girl_last ASC,
            girl_first ASC"""
    )
    fun getPartners(): Flow<List<PartnerDetails>>

    @Query(
        """
            SELECT id
            FROM partner
            WHERE active = 1
            ORDER BY id ASC LIMIT 1
        """
    )
    fun getFirst(): Long

    @Query(
        """
        SELECT
            p.id,
            p.guy_first || ' ' || p.guy_last || ' & ' || p.girl_first || ' ' || p.girl_last 
                AS partnerNames,
            p.active_goal AS goalID,
            g.goal AS goalNm,
            g.reward,
            g.points_needed AS ptsNeeded,
            g.points_earned AS ptsEarned,
            g.active AS goalActive
        FROM partner p
        JOIN goal g
            ON p.active_goal = g.id
        WHERE p.id = :id
        ORDER BY guy_last ASC,
            guy_first ASC,
            girl_last ASC,
            girl_first ASC
        """
    )
    fun getPartner(id: Long): Flow<PartnerDetails>

    @Query(
        """
            SELECT
                p.guy_first,
                p.guy_last,
                p.girl_first,
                p.girl_last,
                p.active,
                p.active_goal
            FROM partner p
            WHERE p.id = :id
        """
    )
    fun getNames(id: Long): PartnerNames

    @Query(
        """
            SELECT
                g.goal,
                g.reward,
                g.points_needed,
                g.points_earned,
                g.active
            FROM
                partner p
            JOIN goal g
                on p.active_goal = g.id
            WHERE p.id =:id
            
        """
    )
    fun getGoalDetails(id: Long): GoalDetails

    @Query(
        """
            SELECT COUNT(id)
            FROM partner
            WHERE guy_first =:guyFirst
                AND guy_last=:guyLast
                AND girl_first=:girlFirst
                AND girl_last=:girlLast
        """
    )
    fun getDuplicateName(guyFirst: String,
                         guyLast: String,
                         girlLast: String,
                         girlFirst: String): Int
}
