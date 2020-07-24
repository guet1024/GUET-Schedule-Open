package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("delete from User")
    void deleteAll();

    @Query("select * from User")
    List<User> selectAll();

    @Query("select username from User")
    List<String> selectAllUserName();

    @Query("select * from User where username=:username")
    List<User> selectUser(String username);

    @Query("delete from User where username=:username")
    void deleteUser(String username);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
}
