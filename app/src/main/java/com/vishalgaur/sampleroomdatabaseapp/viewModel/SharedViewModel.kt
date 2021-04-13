package com.vishalgaur.sampleroomdatabaseapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import com.vishalgaur.sampleroomdatabaseapp.*
import com.vishalgaur.sampleroomdatabaseapp.database.UserData
import com.vishalgaur.sampleroomdatabaseapp.database.UserDatabase
import com.vishalgaur.sampleroomdatabaseapp.isEmailValid
import com.vishalgaur.sampleroomdatabaseapp.isPhoneValid
import com.vishalgaur.sampleroomdatabaseapp.repository.UsersRepository
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "SharedViewModel"

class SharedViewModel(application: Application) :
    AndroidViewModel(application) {

    private val usersRepository = UsersRepository(UserDatabase.getInstance(application))

    var userData: LiveData<UserData?>


    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> get() = _status

    private val _errorStatus = MutableLiveData<ViewErrors>()
    val errorStatus: LiveData<ViewErrors> get() = _errorStatus

    init {
        userData = MutableLiveData()
        refreshDataFromRepository()
    }

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            _status.value = DataStatus.LOADING
            _errorStatus.value = ViewErrors.NONE

            setData()

            try {
                Log.d(TAG, "Getting data from network...")
                usersRepository.refreshData()
            } catch (networkError: IOException) {
                Log.d(TAG, "Not able to get data from network!")
            }

            setStatus(userData.value != null)
        }
    }

    private fun setData() {
        Log.d(TAG, "Getting data from Room...")
        viewModelScope.launch {
            userData = usersRepository.uData
        }
    }

    fun setStatus(dataFound: Boolean) {
        if (dataFound) {
            Log.d(TAG, "Data found: ${userData.value?.userName}")
            _status.value = DataStatus.LOADED
        } else {
            Log.d(TAG, "No Data Found!")
            _status.value = DataStatus.EMPTY
        }
    }

    fun submitData(name: String, email: String, mobile: String, dob: String) {
        if (name.isBlank() || email.isBlank() || mobile.isBlank() || dob.isBlank()) {
            _status.value = DataStatus.EMPTY
            _errorStatus.value = ViewErrors.ERR_EMPTY
        } else {
            var err = "ERROR"
            if (!isEmailValid(email)) {
                err += "_EMAIL"
            }
            if (!isPhoneValid(mobile)) {
                err += "_MOBILE"
            }
            when (err) {
                "ERROR" -> {
                    val newData = UserData(
                        userName = name.trim(),
                        userEmail = email.trim(),
                        userMobile = mobile.trim(),
                        userDOB = dob
                    )
                    insertData(newData)
                    _errorStatus.value = ViewErrors.NONE
                    _status.value = DataStatus.LOADED

                }
                "ERROR_EMAIL" -> _errorStatus.value = ViewErrors.ERR_EMAIL
                "ERROR_MOBILE" -> _errorStatus.value = ViewErrors.ERR_MOBILE
                "ERROR_EMAIL_MOBILE" -> _errorStatus.value = ViewErrors.ERR_EMAIL_MOBILE
            }
        }
    }

    fun clearData() {
        viewModelScope.launch {
            _status.value = DataStatus.EMPTY
            usersRepository.clearData()
        }
    }

    private fun insertData(newData: UserData) {
        viewModelScope.launch {
            _status.value = DataStatus.EMPTY
            usersRepository.updateData(newData)
        }
    }
}