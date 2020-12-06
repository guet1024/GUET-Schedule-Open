package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomizedExamDao {
    @Query("select sts, ets from CustomizedExam where sid=:sid and date=:date and time=:time and week=:week and weekday=:weekday and cno=:cno and comment=:comment")
    List<CustomizedExam.CustomizedSTSAndETS> selectForUserAndExam(@NonNull String sid, @NonNull String date, @NonNull String time, long week, long weekday, @NonNull String cno, @NonNull String comment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CustomizedExam tuple);
}
