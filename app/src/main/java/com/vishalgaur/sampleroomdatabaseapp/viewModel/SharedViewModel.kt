package com.vishalgaur.sampleroomdatabaseapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.vishalgaur.sampleroomdatabaseapp.database.UserData
import kotlinx.coroutines.launch

private const val TAG = "SharedViewModel"
enum class DataStatus {LOADING, LOADED, EMPTY}

class SharedViewModel: ViewModel() {

	private val _userData = MutableLiveData<UserData?>()
	val userData: LiveData<UserData?> get() = _userData

	private val _status = MutableLiveData<DataStatus>()
	val status: LiveData<DataStatus> get() = _status


	init {
	    viewModelScope.launch {
			Log.d(TAG, "getting data...")
	    	_status.value = DataStatus.EMPTY
		}
	}

}