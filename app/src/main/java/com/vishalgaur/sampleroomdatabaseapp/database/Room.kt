package com.vishalgaur.sampleroomdatabaseapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
//    @Query("SELECT * FROM userTable ORDER BY userId DESC")
//    fun getUsersData(): List<UserData>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(usersData: List<UserData>)

    @Query("DELETE FROM userTable")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(uData: UserData)

    @Query("SELECT * FROM userTable WHERE userId = :userId")
    fun getById(userId: Long): UserData?

    @Query("SELECT * FROM userTable ORDER BY userId DESC LIMIT 1")
    fun getNewData(): LiveData<UserData?>
}

@Database(entities = [UserData::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract val userDao: UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "userInfo"
                    )
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }
}