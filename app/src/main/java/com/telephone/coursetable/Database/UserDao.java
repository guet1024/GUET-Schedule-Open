package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * @clear
 */
@Dao
public interface UserDao {
    @Query("delete from User")
    void deleteAll();

    @Query("select * from User")
    List<User> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("select username from User")
    List<String> selectAllUserName();

    @Query("select * from User where username=:username")
    List<User> selectUser(String username);

    @Query("delete from User where username=:username")
    void deleteUser(String username);

    @Query("update User set activated=0")
    void disableAllUser();

    @Query("select * from User where activated=1")
    List<User> getActivatedUser();

    @Query("update User set activated=1 where username=:username")
    void activateUser(String username);


}
