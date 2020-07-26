package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PersonInfoDao {
    @Query("delete from PersonInfo")
    void deleteAll();

    @Query("select * from PersonInfo")
    List<PersonInfo> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PersonInfo tuple);

    @Query("select grade from PersonInfo")
    List<Long> getGrade();
}
