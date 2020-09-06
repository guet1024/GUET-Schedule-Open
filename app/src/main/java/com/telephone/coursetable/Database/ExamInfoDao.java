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
public interface ExamInfoDao {
    @Query("delete from ExamInfo")
    void deleteAll();

    @Query("select * from ExamInfo order by sts ASC")
    List<ExamInfo> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExamInfo tuple);
}
