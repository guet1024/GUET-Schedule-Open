package com.telephone.coursetable.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ExamTotal.class, GradeTotal.class}, version = 2, exportSchema = false)
public abstract class AppDatabaseCompareTest extends RoomDatabase {
    public abstract ExamTotalDao examTotalDao();
    public abstract GradeTotalDao gradeTotalDao();
}
