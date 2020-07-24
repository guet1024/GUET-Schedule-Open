package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TermInfoDao {
    @Query("delete from TermInfo")
    void deleteAll();

    @Query("select * from TermInfo")
    List<TermInfo> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TermInfo tuple);
}
