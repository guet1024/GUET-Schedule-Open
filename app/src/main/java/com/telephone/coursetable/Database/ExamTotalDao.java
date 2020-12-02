package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExamTotalDao {
    @Query("update ExamTotal set read=1")
    void readAll();

    @Query("select count(*) from ExamTotal where read=0")
    int unreadNum();

    @Query("select count(*) from ExamTotal")
    int totalNum();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(ExamTotal tuple);

    @Query("select * from ExamTotal")
    List<ExamTotal> selectAll();
}
