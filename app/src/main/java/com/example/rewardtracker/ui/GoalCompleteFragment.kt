package com.example.rewardtracker.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rewardtracker.R
import com.example.rewardtracker.RewardApplication
import com.example.rewardtracker.databinding.FragmentGoalCompleteBinding
import com.example.rewardtracker.model.PartnerDetails
import com.example.rewardtracker.ui.viewmodel.RewardViewModel
import com.example.rewardtracker.ui.viewmodel.RewardViewModelFactory


class GoalCompleteFragment : Fragment() {

    private val navigationArgs: GoalCompleteFragmentArgs by navArgs()
    private val viewModel: RewardViewModel by activityViewModels {
        RewardViewModelFactory(
            (activity?.application as RewardApplication).database.RewardDao()
        )
    }
    private lateinit var partnerDetails: PartnerDetails
    private var partnerID = 0L

    private var _binding: FragmentGoalCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalCompleteBinding.inflate(inflater, container, false)
        viewModel.activePartner.observe(viewLifecycleOwner) { value ->
            partnerDetails = value
            binding.rewardMsg.text = getString(R.string.reward_msg,partnerDetails.goalNm, partnerDetails.reward)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partnerID = navigationArgs.id
        if (partnerID != partnerDetails.id) {
            viewModel.setActive(partnerID)
        }

        binding.addGoal.setOnClickListener {
            val action = GoalCompleteFragmentDirections
                .actionGoalCompleteFragmentToAddGoalFragment(partnerDetails.id)
            findNavController().navigate(action)
        }
    }
}