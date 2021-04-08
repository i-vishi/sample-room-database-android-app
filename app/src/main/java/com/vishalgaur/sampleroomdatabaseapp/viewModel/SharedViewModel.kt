package com.vishalgaur.sampleroomdatabaseapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "SharedViewModel"

class SharedViewModel: ViewModel() {

	private val _userName = MutableLiveData<String>()
	val name: LiveData<String> get() = _userName

	private val _email = MutableLiveData<String>()
	val email: LiveData<String> get() = _email

	private val _mobile = MutableLiveData<String>()
	val mobile: LiveData<String> get() = _mobile

	private val _dob = MutableLiveData<String>()
	val dob: LiveData<String> get() = _dob

}