package com.vishalgaur.sampleroomdatabaseapp

import android.app.Activity
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.VisibleForTesting
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vishalgaur.sampleroomdatabaseapp.databinding.FragmentHomeBinding
import com.vishalgaur.sampleroomdatabaseapp.viewModel.SharedViewModel
import com.vishalgaur.sampleroomdatabaseapp.viewModel.SharedViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // initializing shared view model
        val application = requireNotNull(this.activity).application
        val viewModelFactory = SharedViewModelFactory(application)
        sharedViewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        setObservers()

        setViews()

        return binding.root
    }

    private fun setViews() {
        binding.fabAddEdit.setOnClickListener {
            findNavController().navigate(R.id.actionAddEditData)
        }

        binding.dataLayout.visibility = View.GONE

        binding.searchBtn.setOnClickListener {
            onSearch()
        }
    }

    private fun onSearch() {
        val query = binding.searchBoxEditText.text.toString()
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view?.windowToken,
            0
        )
        sharedViewModel.searchData(query)
    }

    private fun setObservers() {
        sharedViewModel.status.observe(viewLifecycleOwner, { status ->
            when (status) {
                DataStatus.LOADED -> setTextView()
                else -> setTextView(false)
            }
        })

        sharedViewModel.userData.observe(viewLifecycleOwner, { userData ->
            sharedViewModel.setStatus(userData != null)
        })

        sharedViewModel.searchErrStatus.observe(viewLifecycleOwner, { searchStatus ->
            when (searchStatus) {
                SearchErrors.NONE -> setSearchViews(searchStatus, View.GONE)
                else -> setSearchViews(searchStatus, View.VISIBLE)
            }
        })

        sharedViewModel.searchedData.observe(viewLifecycleOwner, { searchResult ->
            if (searchResult != null) {
                setHomeView()
                binding.detailName.text = searchResult.userName
                binding.detailEmail.text = searchResult.userEmail
                binding.detailMobile.text = searchResult.userMobile
                binding.detailDob.text = searchResult.userDOB
            } else {
                setHomeView(View.GONE, View.VISIBLE)
            }
        })
    }

    private fun setHomeView(dataVisibility: Int = View.VISIBLE, textVisibility: Int = View.GONE) {
        binding.dataLayout.visibility = dataVisibility
        binding.homeEmptyTextView.visibility = textVisibility
    }

    private fun setSearchViews(errors: SearchErrors, errVisibility: Int) {
        binding.searchErrorTextView.visibility = errVisibility
        when (errors) {
            SearchErrors.NONE -> Unit
            SearchErrors.ERR_INVALID -> binding.searchErrorTextView.text =
                getString(R.string.search_invalid_query_text)
            SearchErrors.ERR_EMPTY -> binding.searchErrorTextView.text =
                getString(R.string.search_empty_query_text)
            SearchErrors.ERR_NOT_FOUND -> binding.searchErrorTextView.text =
                getString(R.string.search_not_found_text)
        }
    }

    private fun setTextView(hasData: Boolean? = true) {
        binding.homeEmptyTextView.apply {
            when (hasData) {
                true -> {
                    setText(R.string.detail_search_true_text)
                    setCompoundDrawables(null, null, null, null)
                }
                false -> {
                    setText(R.string.detail_empty_text)
                    setCompoundDrawables(
                        null,
                        getDrawable(context, R.drawable.ic_error_outline_64),
                        null,
                        null
                    )
                }
            }
        }
    }
}