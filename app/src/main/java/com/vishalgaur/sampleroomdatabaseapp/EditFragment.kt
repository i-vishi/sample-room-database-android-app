package com.vishalgaur.sampleroomdatabaseapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vishalgaur.sampleroomdatabaseapp.database.UserData
import com.vishalgaur.sampleroomdatabaseapp.databinding.FragmentEditBinding
import com.vishalgaur.sampleroomdatabaseapp.viewModel.SharedViewModel
import com.vishalgaur.sampleroomdatabaseapp.viewModel.SharedViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var sharedViewModel: SharedViewModel

    private val calInstance: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(inflater, container, false)

        // initializing shared view model
        val application = requireNotNull(this.activity).application
        val viewModelFactory = SharedViewModelFactory(application)
        sharedViewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        setViews()

        setObservers()

        return binding.root
    }

    private fun setObservers() {
        sharedViewModel.errorStatus.observe(viewLifecycleOwner, { err ->
            modifyErrors(err)
        })
    }

    private fun setViews() {
        binding.inputCancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.inputSaveBtn.setOnClickListener {
            onSubmit()
            if (sharedViewModel.errorStatus.value == ViewErrors.NONE) {
                findNavController().navigateUp()
            }
        }

        binding.inputSelectDateBtn.setOnClickListener { showDatePicker() }

        binding.inputErrorTextView.visibility = View.GONE
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calInstance.set(Calendar.YEAR, year)
            calInstance.set(Calendar.MONTH, month)
            calInstance.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDateInTextView()
        }

        context?.let {
            DatePickerDialog(
                it,
                dateSetListener,
                calInstance.get(Calendar.YEAR),
                calInstance.get(Calendar.MONTH),
                calInstance.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    private fun setDateInTextView() {
        val format = "dd/MM/yyyy"
        binding.inputDobEditText.setText(
            SimpleDateFormat(
                format,
                Locale.US
            ).format(calInstance.time)
        )
    }

    private fun onSubmit() {
        val id = binding.inputIdEditText.text.toString()
        val name = binding.inputNameEditText.text.toString()
        val email = binding.inputEmailEditText.text.toString()
        val mobile = binding.inputMobileEditText.text.toString()
        val dob = binding.inputDobEditText.text.toString()

        sharedViewModel.submitData(id, name, email, mobile, dob)
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