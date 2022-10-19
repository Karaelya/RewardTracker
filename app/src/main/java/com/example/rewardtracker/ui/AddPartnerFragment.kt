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
import com.example.rewardtracker.databinding.FragmentAddPartnerBinding
import com.example.rewardtracker.model.PartnerNames
import com.example.rewardtracker.ui.viewmodel.RewardViewModel
import com.example.rewardtracker.ui.viewmodel.RewardViewModelFactory
import kotlinx.coroutines.*

class AddPartnerFragment : Fragment() {

    private lateinit var partnerName: PartnerNames
    private val navigationArgs: AddPartnerFragmentArgs by navArgs()

    private var _binding: FragmentAddPartnerBinding? = null
    private val binding get() = _binding!!

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var partnerID: Long = 0L

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
        _binding = FragmentAddPartnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        partnerID = navigationArgs.id

        if (partnerID != 0L) {

            partnerName = viewModel.getName(partnerID)
            bindPartner(partnerName)

            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                deletePartner()
            }
            binding.saveBtn.setOnClickListener{
                uiScope.launch(Dispatchers.IO) {
                    updatePartner()
                }
            }
        } else {
            binding.saveBtn.setOnClickListener {
                uiScope.launch(Dispatchers.IO) {
                    addPartner()
                }
                val action = AddPartnerFragmentDirections
                    .actionAddPartnerFragmentToAddGoalFragment(0L)
                findNavController().navigate(action)
            }
            binding.deleteBtn.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deletePartner(){
        viewModel.deletePartner(partnerID)
        findNavController().navigate(
            R.id.action_addPartnerFragment_to_partnerListFragment
        )
    }

    private fun addPartner() {
        // CHECK FOR DUPLICATE NAME BEFORE RUNNING THIS
        // IF NAME IS A DUPLICATE OPEN POPUP WINDOW ASKING IF THE
        //      USER WANTS TO REACTIVATE THE CLOSED PARTNERSHIP OR
        //      CANCEL IN WHICH CASE THEY COME BACK TO THIS SCREEN
        //      AND CHANGE AT LEAST ONE PARTNER NAME

        if (isValidEntry()) {
           partnerID = viewModel.addNewPartner(
                binding.guyFirstInput.text.toString(),
                binding.guyLastInput.text.toString(),
                binding.girlFirstInput.text.toString(),
                binding.girlLastInput.text.toString()
            )
        }
    }

    private fun isValidEntry() = viewModel.isValidEntry(
        binding.guyFirstInput.text.toString(),
        binding.guyLastInput.text.toString(),
        binding.girlFirstInput.text.toString(),
        binding.guyLastInput.text.toString()
    )

    private fun updatePartner() {
        Log.d(TAG, "Update Partner Started")
        if (isValidEntry()) {
            viewModel.updatePartner(
                partnerID,
                binding.guyFirstInput.text.toString(),
                binding.guyLastInput.text.toString(),
                binding.girlFirstInput.text.toString(),
                binding.girlLastInput.text.toString()
            )
        }
    }

    private fun bindPartner(partnerName: PartnerNames) {
        binding.apply {
            guyFirstInput.setText(partnerName.guyFirst)
            guyLastInput.setText(partnerName.guyLast)
            girlFirstInput.setText(partnerName.girlFirst)
            girlLastInput.setText(partnerName.girlLast)
        }
    }
}