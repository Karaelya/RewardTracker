package com.example.rewardtracker.ui.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.rewardtracker.data.RewardDao
import com.example.rewardtracker.model.GoalDetails
import com.example.rewardtracker.model.PartnerDetails
import com.example.rewardtracker.model.PartnerNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RewardViewModel(
    private val rewardDao: RewardDao
) : ViewModel() {

    val partnerList: LiveData<List<PartnerDetails>> = rewardDao.getPartners().asLiveData()
    var partnerID: Long = 0L
    var activePartner: LiveData<PartnerDetails> = getPartner(getFirst())

    private fun getPartner(id: Long): LiveData<PartnerDetails> {
        return rewardDao.getPartner(id).asLiveData()
    }

    private fun getFirst(): Long {
        return rewardDao.getFirst()
    }

    fun getName(id: Long): PartnerNames {
        return rewardDao.getNames(id)
    }

    fun getGoalDetails(id: Long): GoalDetails {
        return rewardDao.getGoalDetails(id)
    }

    fun addNewPartner(
        guyFirst: String,
        guyLast: String,
        girlFirst: String,
        girlLast: String
    ) {
        val newPartner = PartnerNames(
            guyFirst = guyFirst,
            guyLast = guyLast,
            girlFirst = girlFirst,
            girlLast = girlLast,
            active = true,
            activeGoal = 0L
        )

        //RETURN NEW PARTNER ID FROM DATABASE AND STORE IT IN partnerID

        partnerID = rewardDao.addPartner(newPartner)
        setActive(partnerID)
        addGoal("Undefined","Undecided",10)

    }

    fun updatePartner(
        id: Long,
        guyFirst: String,
        guyLast: String,
        girlFirst: String,
        girlLast: String,
    ) {

        rewardDao.updatePartner(id,guyFirst,guyLast,girlFirst,girlLast)
    }

    fun deletePartner(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            rewardDao.deletePartner(id)
            setActive(getFirst())
        }
    }

    fun isValidEntry(
        guyLast: String,
        guyFirst: String,
        girlLast: String,
        girlFirst: String
    ): Boolean {
        return guyLast.isNotBlank()
                && guyFirst.isNotBlank()
                && girlLast.isNotBlank()
                && girlFirst.isNotBlank()
                && rewardDao.getDuplicateName(
                    guyFirst,guyLast,girlFirst,girlLast) == 0
    }

    fun addGoal(
        goal: String,
        reward: String,
        pointsNeeded: Int,
    ){
        val newGoal = GoalDetails (
            goal = goal,
            reward = reward,
            pointsNeeded = pointsNeeded,
            pointsEarned = 0,
            active = true
        )
        rewardDao.changeGoal(rewardDao.addGoal(newGoal), partnerID)

    }

    fun updateGoal(
        partnerID: Long,
        goal: String,
        reward: String,
        pointsNeeded: Int
    ) {
        rewardDao.updateGoal(partnerID, goal, reward, pointsNeeded)

    }

    fun updatePts(ptsEarned: Int, partnerID: Long) {
        Log.d(TAG, "$ptsEarned $partnerID")
        rewardDao.updatePts(ptsEarned,partnerID)
    }

    fun checkGoal(partnerID: Long): Boolean {
        return(rewardDao.checkGoal(partnerID))
    }

    fun setActive(partnerID: Long) {
        activePartner = rewardDao.getPartner(partnerID).asLiveData()
    }


    fun isValidEntryGoal(
        goal: String,
        reward: String,
        pointsNeeded: Int
    ): Boolean {
        return goal.isNotBlank()
                && reward.isNotBlank()
                && pointsNeeded > 0
    }
}

class RewardViewModelFactory(private val rewardDao: RewardDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RewardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RewardViewModel(rewardDao) as T
        }
        throw  IllegalArgumentException("Unknown ViewModel class")
    }
}