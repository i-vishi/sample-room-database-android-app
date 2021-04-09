package com.vishalgaur.sampleroomdatabaseapp.repository

import android.util.Log
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

    suspend fun updateData(newData: UserData) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "updating data on Room")
            database.userDao.clear()
            database.userDao.insert(newData)
            Log.d(TAG, "updating data on Network...")
        }
    }

    val uData: UserData? = database.userDao.getNewData()
}