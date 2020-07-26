package com.telephone.coursetable.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GoToClass.class, ClassInfo.class, TermInfo.class, User.class, PersonInfo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GoToClassDao goToClassDao();
    public abstract ClassInfoDao classInfoDao();
    public abstract TermInfoDao termInfoDao();
    public abstract UserDao userDao();
    public abstract PersonInfoDao personInfoDao();
}
