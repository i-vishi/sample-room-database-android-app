package com.vishalgaur.sampleroomdatabaseapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.vishalgaur.sampleroomdatabaseapp.DataStatus
import com.vishalgaur.sampleroomdatabaseapp.ViewErrors
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

//    private val _userData = MutableLiveData<UserData?>()
//    val userData: LiveData<UserData?> get() = _userData

    val userData = usersRepository.uData

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> get() = _status

    private val _errorStatus = MutableLiveData<ViewErrors>()
    val errorStatus: LiveData<ViewErrors> get() = _errorStatus

    init {
        refreshDataFromRepository()
    }

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            _status.value = DataStatus.LOADING
            _errorStatus.value = ViewErrors.NONE

            try {
                Log.d(TAG, "Getting data from network...")
                usersRepository.refreshData()
            } catch (networkError: IOException) {
                Log.d(TAG, "Not able to get data from network!")
            }

            userData.observeForever {
                Log.d(TAG, "Getting data from Room...")

                if (it != null) {
                    Log.d(TAG, "Data found: ${userData.value?.userName}")
                    _status.value = DataStatus.LOADED
                } else {
                    Log.d(TAG, "No Data Found!")
                    _status.value = DataStatus.EMPTY
                }
            }
        }
    }

//    private fun getUserFromDatabase(): UserData? {
//        val uData = db.getNewData()
//        val tableSize = db.getUsersData().size
//        if (uData != null) {
//            Log.d(TAG, "size: $tableSize, data: $uData")
//            return uData
//        }
//        return null
//    }

    fun submitData(name: String, email: String, mobile: String, dob: String) {
        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || dob.isEmpty()) {
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