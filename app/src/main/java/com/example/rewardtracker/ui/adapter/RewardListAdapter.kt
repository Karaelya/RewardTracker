package com.example.rewardtracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rewardtracker.databinding.ItemListPartnerBinding
import com.example.rewardtracker.model.PartnerDetails

class RewardListAdapter(
    private val onItemClicked: (PartnerDetails) -> Unit
) : ListAdapter<PartnerDetails, RewardListAdapter.RewardViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RewardViewHolder {
        val layoutInflater = LayoutInflater.from(   parent.context)
        return RewardViewHolder(
            ItemListPartnerBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: RewardViewHolder,
        position: Int
    ) {
        val partnerDetails = getItem(position)

        holder.itemView.setOnClickListener{
            onItemClicked(partnerDetails)
        }
        holder.bind(partnerDetails)
    }



    class RewardViewHolder(
        private var binding: ItemListPartnerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(partnerDetails: PartnerDetails) {
            binding.partnerDetails = partnerDetails
            binding.executePendingBindings()
        }
    }

/*
RecyclerView.ViewHolder(binding.root) {

*/

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<PartnerDetails>() {
            override fun areItemsTheSame(
                oldItem: PartnerDetails,
                newItem: PartnerDetails
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: PartnerDetails,
                newItem: PartnerDetails
            ): Boolean {
                return (oldItem.partnerNames == newItem.partnerNames &&
                        oldItem.goalID == newItem.goalID &&
                        oldItem.ptsNeeded == newItem.ptsNeeded &&
                        oldItem.ptsEarned == newItem.ptsEarned &&
                        oldItem.goalNm == newItem.goalNm)
            }
        }
    }

}