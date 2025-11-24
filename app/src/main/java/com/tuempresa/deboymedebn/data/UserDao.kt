
package com.tuempresa.deboymedebn.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tuempresa.deboymedebn.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>
}
