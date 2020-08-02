package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GraduationScoreDao {
    @Query("select * from GraduationScore order by sterm DESC, scname ASC")
    List<GraduationScore> selectAll();

    @Query("delete from GraduationScore")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GraduationScore tuple);
}
