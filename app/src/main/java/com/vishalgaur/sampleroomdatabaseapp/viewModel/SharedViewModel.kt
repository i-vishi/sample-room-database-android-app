package com.vishalgaur.sampleroomdatabaseapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
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

    private val _searchedData = MutableLiveData<UserData?>()
    val searchedData: LiveData<UserData?> get() = _searchedData

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> get() = _status

    private val _errorStatus = MutableLiveData<ViewErrors>()
    val errorStatus: LiveData<ViewErrors> get() = _errorStatus

    private val _searchErrStatus = MutableLiveData<SearchErrors>()
    val searchErrStatus: LiveData<SearchErrors> get() = _searchErrStatus

    init {
        userData = MutableLiveData()
        refreshDataFromRepository()
    }

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            _status.value = DataStatus.LOADING
            _errorStatus.value = ViewErrors.NONE
            _searchErrStatus.value = SearchErrors.NONE

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

    fun searchData(userId: String) {
        Log.d(TAG, "QUERY: $userId")
        when {
            userId.isBlank() -> {
                _searchErrStatus.value = SearchErrors.ERR_EMPTY
            }
            userId != userId.toLong().toString() -> {
                _searchErrStatus.value = SearchErrors.ERR_INVALID
            }
            else -> {
                getData(userId.toLong())
            }
        }
    }

    fun submitData(id: String, name: String, email: String, mobile: String, dob: String) {
        if (id.isBlank() || name.isBlank() || email.isBlank() || mobile.isBlank() || dob.isBlank()) {
            _status.value = DataStatus.EMPTY
            _errorStatus.value = ViewErrors.ERR_EMPTY
        } else {
            var err = ERR_INIT
            if (!isEmailValid(email)) {
                err += ERR_EMAIL
            }
            if (!isPhoneValid(mobile)) {
                err += ERR_MOBILE
            }
            when (err) {
                ERR_INIT -> {
                    _errorStatus.value = ViewErrors.NONE
                    _status.value = DataStatus.LOADED
                    val newData =
                        UserData(id.toLong(), name.trim(), email.trim(), mobile.trim(), dob)
                    insertData(newData)
                }
                (ERR_INIT + ERR_EMAIL) -> _errorStatus.value = ViewErrors.ERR_EMAIL
                (ERR_INIT + ERR_MOBILE) -> _errorStatus.value = ViewErrors.ERR_MOBILE
                (ERR_INIT + ERR_EMAIL + ERR_MOBILE) -> _errorStatus.value =
                    ViewErrors.ERR_EMAIL_MOBILE
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
            usersRepository.insertData(newData)
        }
    }

    private fun getData(uId: Long) {
        viewModelScope.launch {
            _searchErrStatus.value = SearchErrors.NONE
            _searchedData.value = usersRepository.uDataById(uId)
            if (_searchedData.value == null) {
                _searchErrStatus.value = SearchErrors.ERR_NOT_FOUND
            }
        }
    }
}