package com.vishalgaur.sampleroomdatabaseapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.vishalgaur.sampleroomdatabaseapp.database.UserData
import com.vishalgaur.sampleroomdatabaseapp.isEmailValid
import com.vishalgaur.sampleroomdatabaseapp.isPhoneValid
import kotlinx.coroutines.launch

private const val TAG = "SharedViewModel"

enum class DataStatus { LOADING, LOADED, EMPTY }

enum class ViewErrors { NONE, ERR_EMAIL, ERR_MOBILE, ERR_EMAIL_MOBILE, ERR_EMPTY }

class SharedViewModel : ViewModel() {

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> get() = _status

    private val _errorStatus = MutableLiveData<ViewErrors>()
    val errorStatus: LiveData<ViewErrors> get() = _errorStatus

    init {
        viewModelScope.launch {
            Log.d(TAG, "getting data...")
            _status.value = DataStatus.EMPTY
            _errorStatus.value = ViewErrors.NONE
        }
    }

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
                    _userData.value = UserData(
                        userName = name.trim(),
                        userEmail = email.trim(),
                        userMobile = mobile.trim(),
                        userDOB = dob
                    )
                    _errorStatus.value = ViewErrors.NONE
                    _status.value = DataStatus.LOADED
                }
                "ERROR_EMAIL" -> _errorStatus.value = ViewErrors.ERR_EMAIL
                "ERROR_MOBILE" -> _errorStatus.value = ViewErrors.ERR_MOBILE
                "ERROR_EMAIL_MOBILE" -> _errorStatus.value = ViewErrors.ERR_EMAIL_MOBILE
            }
        }
    }

}