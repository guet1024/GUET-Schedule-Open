package com.telephone.coursetable.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Mask.class}, version = 1, exportSchema = false)
public abstract class AppDatabaseComments extends RoomDatabase {
    public abstract MaskDao maskDao();
}
