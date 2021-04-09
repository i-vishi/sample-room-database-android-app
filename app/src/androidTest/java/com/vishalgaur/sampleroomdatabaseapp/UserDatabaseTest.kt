package com.vishalgaur.sampleroomdatabaseapp

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vishalgaur.sampleroomdatabaseapp.database.UserDao
import com.vishalgaur.sampleroomdatabaseapp.database.UserData
import com.vishalgaur.sampleroomdatabaseapp.database.UserDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class UserDatabaseTest {

    private lateinit var userDao: UserDao
    private lateinit var db: UserDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun insertAndGetUser() {
        val user = UserData(
            123,
            "Vishal Gaur",
            "vishal@email.com",
            "9879879879",
            "01/01/1999"
        )
        userDao.insert(user)
        val getUser = userDao.get("9879879879")
        assertEquals(getUser, user)
    }

}