package com.example.rewardtracker.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rewardtracker.R
import com.example.rewardtracker.RewardApplication
import com.example.rewardtracker.databinding.FragmentAddGoalBinding
import com.example.rewardtracker.model.GoalDetails
import com.example.rewardtracker.model.PartnerDetails
import com.example.rewardtracker.ui.viewmodel.RewardViewModel
import com.example.rewardtracker.ui.viewmodel.RewardViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

class AddGoalFragment : Fragment() {

    private lateinit var goalDetails: GoalDetails
    private val navigationArgs: AddGoalFragmentArgs by navArgs()

    private var _binding: FragmentAddGoalBinding? = null
    private val binding get() = _binding!!
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    private var partnerID: Long = 0L
    private lateinit var partnerDetails: PartnerDetails

    private val viewModel: RewardViewModel by activityViewModels {
        RewardViewModelFactory(
            (activity?.application as RewardApplication).database.RewardDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        viewModel.activePartner.observe(viewLifecycleOwner) { value ->
            partnerDetails = value
            partnerID = partnerDetails.id
            changePartner()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        partnerID = navigationArgs.id
        if (partnerID != partnerDetails.id) {
            viewModel.setActive(partnerID)
        } else (
                changePartner()
        )

        binding.cancelButton.setOnClickListener {
            cancelGoal()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addGoal() {
        if (isValidEntryGoal()) {
            viewModel.addGoal(
                binding.goalInput.text.toString(),
                binding.rewardInput.text.toString(),
                binding.pointsNeededInput.text.toString().toInt()
            )
            val action = AddGoalFragmentDirections
                .actionAddGoalFragmentToPartnerDetailFragment(partnerID)
            findNavController().navigate(action)
        }
    }

    private fun changePartner() {
        uiScope.launch(Dispatchers.IO) {
            if (viewModel.checkGoal(partnerID)) {
                goalDetails = viewModel.getGoalDetails(partnerID)
                bindGoal(goalDetails)

                binding.saveBtn.setOnClickListener {
                    updateGoal()
                }
                binding.cancelButton.visibility = View.VISIBLE
            } else {
                binding.goalInput.setText("")
                binding.rewardInput.setText("")
                binding.pointsNeededInput.setText("")

                binding.saveBtn.setOnClickListener {
                    addGoal()
                }
                binding.cancelButton.visibility = View.GONE
            }
        }
    }

    private fun cancelGoal(){
        val action = AddGoalFragmentDirections
            .actionAddGoalFragmentToPartnerDetailFragment(partnerID)
        findNavController().navigate(action)
    }

    private fun isValidEntryGoal() = viewModel.isValidEntryGoal(
        binding.goalInput.text.toString(),
        binding.rewardInput.text.toString(),
        binding.pointsNeededInput.text.toString().toInt()
    )

    private fun updateGoal() {
        if (isValidEntryGoal()) {
            viewModel.updateGoal(
                partnerID,
                binding.goalInput.text.toString(),
                binding.rewardInput.text.toString(),
                binding.pointsNeededInput.text.toString().toInt(),
            )
            val action = AddGoalFragmentDirections
                .actionAddGoalFragmentToPartnerDetailFragment(partnerID)
            findNavController().navigate(action)
        }

    }

    private fun bindGoal(goalDetails: GoalDetails) {
        Log.d(TAG, "Binding Started")
        binding.apply {
            goalInput.setText(goalDetails.goal)
            rewardInput.setText(goalDetails.reward)
            pointsNeededInput.setText(goalDetails.pointsNeeded).toString()
        Log.d(TAG, "Binding Finished")
        }

    }

}