package com.example.rewardtracker.ui

import androidx.fragment.app.Fragment
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rewardtracker.R
import com.example.rewardtracker.RewardApplication
import com.example.rewardtracker.databinding.FragmentDetailPartnerBinding
import com.example.rewardtracker.model.PartnerDetails
import com.example.rewardtracker.ui.viewmodel.RewardViewModel
import com.example.rewardtracker.ui.viewmodel.RewardViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PartnerDetailFragment : Fragment(){

    private val navigationArgs: PartnerDetailFragmentArgs by navArgs()
    private val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    private var partnerID: Long = 0L

    private val viewModel: RewardViewModel by activityViewModels {
        RewardViewModelFactory(
            (activity?.application as RewardApplication).database.RewardDao()
        )
    }

    private lateinit var partnerDetails: PartnerDetails

    private var _binding: FragmentDetailPartnerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPartnerBinding.inflate(inflater, container, false )
        viewModel.activePartner.observe(viewLifecycleOwner) { value ->
            partnerDetails = value
            bindPartnerDetails(partnerDetails)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partnerID = navigationArgs.id
        if (partnerID != partnerDetails.id) {
            viewModel.setActive(partnerID)
        }

        binding.add1Btn.setOnClickListener{
            add1()
        }
        binding.subtract1Btn.setOnClickListener{
            subtract1()
        }
        binding.editPartnerBtn.setOnClickListener{
            findNavController().navigate(
                R.id.action_partnerDetailFragment_to_addPartnerFragment
            )
        }
        binding.editGoalBtn.setOnClickListener{
            val action = PartnerDetailFragmentDirections
                .actionPartnerDetailFragmentToAddPartnerFragment(partnerID)
            findNavController().navigate(action)
        }
    }

    private fun add1(){
       val add1 = partnerDetails.ptsEarned + 1
       uiScope.launch(Dispatchers.IO) {
           viewModel.updatePts(add1, partnerID)
       }
       if (add1 >= partnerDetails.ptsNeeded){
                val action = PartnerDetailFragmentDirections
                    .actionPartnerDetailFragmentToGoalCompleteFragment(partnerDetails.id)
                findNavController().navigate(action)
       }
    }

    private fun subtract1(){
        uiScope.launch(Dispatchers.IO) {
            if (partnerDetails.ptsEarned > 0) {
                val sub1 = partnerDetails.ptsEarned - 1
                viewModel.updatePts(sub1, partnerID)
            }
        }

    }

    private fun bindPartnerDetails(partnerDetails: PartnerDetails) {
        binding.apply {
            partnerName.text = partnerDetails.partnerNames
            partnerGoal.text = partnerDetails.goalNm
            rewardType.text = partnerDetails.reward
            pointsNeeded.text = getString(R.string.points_needed, partnerDetails.ptsNeeded.toString())
            pointsEarned.text = getString(R.string.points_earned, partnerDetails.ptsEarned.toString())
        }
        Log.d(TAG, "Bind Partner Details Completed")
    }
}