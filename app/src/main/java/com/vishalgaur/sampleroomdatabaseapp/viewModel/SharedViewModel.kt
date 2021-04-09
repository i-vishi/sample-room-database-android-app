package com.vishalgaur.sampleroomdatabaseapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.vishalgaur.sampleroomdatabaseapp.database.UserData
import com.vishalgaur.sampleroomdatabaseapp.database.UserDatabase
import com.vishalgaur.sampleroomdatabaseapp.isEmailValid
import com.vishalgaur.sampleroomdatabaseapp.isPhoneValid
import com.vishalgaur.sampleroomdatabaseapp.repository.UsersRepository
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "SharedViewModel"

enum class DataStatus { LOADING, LOADED, EMPTY }

enum class ViewErrors { NONE, ERR_EMAIL, ERR_MOBILE, ERR_EMAIL_MOBILE, ERR_EMPTY }

class SharedViewModel(application: Application) :
    AndroidViewModel(application) {

    private val usersRepository = UsersRepository(UserDatabase.getInstance(application))

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

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

            Log.d(TAG, "Getting data from Room...")
            val uData = usersRepository.uData
            if (uData != null) {
                Log.d(TAG, "Data found: ${uData.userName}")
                _status.value = DataStatus.LOADED
                _userData.value = uData
            } else {
                Log.d(TAG, "No Data Found!")
                _status.value = DataStatus.EMPTY
                _userData.value = null
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
                    viewModelScope.launch {
                        _userData.value = UserData(
                            userName = name.trim(),
                            userEmail = email.trim(),
                            userMobile = mobile.trim(),
                            userDOB = dob
                        )
                        insertData()
                        _errorStatus.value = ViewErrors.NONE
                        _status.value = DataStatus.LOADED
                    }

                }
                "ERROR_EMAIL" -> _errorStatus.value = ViewErrors.ERR_EMAIL
                "ERROR_MOBILE" -> _errorStatus.value = ViewErrors.ERR_MOBILE
                "ERROR_EMAIL_MOBILE" -> _errorStatus.value = ViewErrors.ERR_EMAIL_MOBILE
            }
        }
    }

    private fun insertData() {
        viewModelScope.launch {
            _status.value = DataStatus.EMPTY

            _userData.value?.let { usersRepository.updateData(it) }
        }
    }
}