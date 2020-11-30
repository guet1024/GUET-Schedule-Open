package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VersionDao {
    @Query("select * from Version where version_name=:v")
    List<Version> selectVersion(@NonNull String v);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Version tuple);
}
