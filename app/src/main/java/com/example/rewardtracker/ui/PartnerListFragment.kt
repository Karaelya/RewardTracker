package com.example.rewardtracker.ui


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.rewardtracker.RewardApplication
import com.example.rewardtracker.databinding.FragmentListPartnerBinding
import com.example.rewardtracker.model.PartnerDetails
import com.example.rewardtracker.ui.adapter.RewardListAdapter
import com.example.rewardtracker.ui.viewmodel.RewardViewModel
import com.example.rewardtracker.ui.viewmodel.RewardViewModelFactory

class PartnerListFragment : Fragment() {
    private val viewModel: RewardViewModel by activityViewModels {
        RewardViewModelFactory(
            (activity?.application as RewardApplication).database.RewardDao()
        )
    }

    private var _binding: FragmentListPartnerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPartnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RewardListAdapter {partnerDetails ->
            getDestination(partnerDetails)

        }
        viewModel.partnerList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.apply {
            recyclerView.adapter = adapter
            addPartnerFab.setOnClickListener {
                val action = PartnerListFragmentDirections
                    .actionPartnerListFragmentToAddPartnerFragment(0L)
                findNavController().navigate(action)
            }
        }
    }

    private fun getDestination(partnerDetails: PartnerDetails) {
        if (partnerDetails.ptsEarned >= partnerDetails.ptsNeeded) {
            val action = PartnerListFragmentDirections
                .actionPartnerListFragmentToGoalCompleteFragment(partnerDetails.id)
            findNavController().navigate(action)
        } else {
            val action = PartnerListFragmentDirections
                .actionPartnerListFragmentToPartnerDetailFragment(partnerDetails.id)
            findNavController().navigate(action)
        }
    }
}
