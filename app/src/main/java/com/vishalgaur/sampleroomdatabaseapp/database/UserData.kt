package com.vishalgaur.sampleroomdatabaseapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userTable")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0L,
    var userName: String,
    var userEmail: String,
    var userMobile: String,
    var userDOB: String
)
