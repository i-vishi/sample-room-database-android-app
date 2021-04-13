package com.vishalgaur.sampleroomdatabaseapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.vishalgaur.sampleroomdatabaseapp.database.UserData
import com.vishalgaur.sampleroomdatabaseapp.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "UsersRepository"

class UsersRepository(private val database: UserDatabase) {

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "refreshData is called")
        }
    }

    suspend fun insertData(newData: UserData) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "updating data on Room")
            database.userDao.insert(newData)
            Log.d(TAG, "updating data on Network...")
        }
    }

    suspend fun clearData() {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "removing from Room")
            database.userDao.clear()
            Log.d(TAG, "removing from Network")
        }
    }

    val uData: LiveData<UserData?> = database.userDao.getNewData()

    val uDataById = fun(uId: Long): UserData? { return database.userDao.getById(uId) }
}