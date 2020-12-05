package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomizedExamDao {
    @Query("select sts, ets from CustomizedExam where sid=:sid and room=:room and date=:date and time=:time")
    List<CustomizedExam.CustomizedSTSAndETS> selectForUserAndExam(@NonNull String sid, @NonNull String room, @NonNull String date, @NonNull String time);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CustomizedExam tuple);
}
