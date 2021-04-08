package com.vishalgaur.sampleroomdatabaseapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.vishalgaur.sampleroomdatabaseapp.databinding.FragmentHomeBinding
import com.vishalgaur.sampleroomdatabaseapp.viewModel.DataStatus
import com.vishalgaur.sampleroomdatabaseapp.viewModel.SharedViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.status.observe(viewLifecycleOwner, { status ->
			when (status) {
				DataStatus.LOADED -> setHomeView()
				else -> setHomeView(View.GONE, View.VISIBLE, false)
			}
		})

        sharedViewModel.userData.observe(viewLifecycleOwner, { userData ->
			if (userData != null) {
				binding.detailName.text = userData.userName
				binding.detailEmail.text = userData.userEmail
				binding.detailMobile.text = userData.userMobile
				binding.detailDob.text = userData.userDOB
			}
		})
        binding.fabAddEdit.setOnClickListener {
            findNavController().navigate(R.id.actionAddEditData)
        }
    }

    private fun setHomeView(
		homeVisibility: Int = View.VISIBLE,
		emptyTextVisibility: Int = View.GONE,
		fabIcon: Boolean = true
	) {
        binding.homeConstraintLayout.visibility = homeVisibility
        binding.homeEmptyTextView.visibility = emptyTextVisibility
        binding.fabAddEdit.setImageResource(if (fabIcon) R.drawable.ic_edit_48 else R.drawable.ic_add_48)
    }
}