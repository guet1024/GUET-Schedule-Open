package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClassInfoDao {
    @Query("delete from ClassInfo")
    void deleteAll();

    @Query("select * from ClassInfo")
    List<ClassInfo> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClassInfo tuple);
}
