package com.vishalgaur.sampleroomdatabaseapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.vishalgaur.sampleroomdatabaseapp.database.UserData
import com.vishalgaur.sampleroomdatabaseapp.databinding.FragmentEditBinding
import com.vishalgaur.sampleroomdatabaseapp.viewModel.SharedViewModel
import com.vishalgaur.sampleroomdatabaseapp.viewModel.ViewErrors

class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViews()

        setObservers()
    }

    private fun setObservers() {
        sharedViewModel.userData.observe(viewLifecycleOwner, { userData ->
            if (userData != null) {
                fillTextView(userData)
            }
        })

        sharedViewModel.errorStatus.observe(viewLifecycleOwner, { err ->
            modifyErrors(err)
        })
    }

    private fun setViews() {
        binding.inputCancelBtn.setOnClickListener {
            findNavController().navigate(R.id.actionGoBackToHome)
        }

        binding.inputSaveBtn.setOnClickListener {
            onSubmit()
            if (sharedViewModel.errorStatus.value == ViewErrors.NONE) {
                findNavController().navigate(R.id.actionGoBackToHome)
            }
        }

        binding.inputErrorTextView.visibility = View.GONE
    }

    private fun onSubmit() {
        val name = binding.inputNameEditText.text.toString()
        val email = binding.inputEmailEditText.text.toString()
        val mobile = binding.inputMobileEditText.text.toString()
        val dob = binding.inputDobEditText.text.toString()

        sharedViewModel.submitData(name, email, mobile, dob)
    }

    private fun fillTextView(userData: UserData) {
        binding.inputNameEditText.setText(userData.userName)
        binding.inputEmailEditText.setText(userData.userEmail)
        binding.inputMobileEditText.setText(userData.userMobile)
        binding.inputDobEditText.setText(userData.userDOB)
    }

    private fun modifyErrors(err: ViewErrors) {
        when (err) {
            ViewErrors.NONE -> setEditTextsError()
            ViewErrors.ERR_EMAIL -> setEditTextsError(emailError = EMAIL_ERROR)
            ViewErrors.ERR_MOBILE -> setEditTextsError(mobError = MOB_ERROR)
            ViewErrors.ERR_EMAIL_MOBILE -> setEditTextsError(EMAIL_ERROR, MOB_ERROR)
            ViewErrors.ERR_EMPTY -> binding.inputErrorTextView.visibility = View.VISIBLE
        }
    }

    private fun setEditTextsError(emailError: String? = null, mobError: String? = null) {
        binding.inputEmailEditText.error = emailError
        binding.inputMobileEditText.error = mobError
        binding.inputErrorTextView.visibility = View.GONE
    }
}