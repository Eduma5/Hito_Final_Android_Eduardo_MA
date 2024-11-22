// UserDao.java
package com.example.tapsoul;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users ORDER BY score DESC")
    List<User> getAllUsers();

    @Query("SELECT * FROM users ORDER BY name ASC")
    List<User> getUsersByName();

    @Query("SELECT * FROM users ORDER BY score DESC")
    List<User> getUsersByScore();
}