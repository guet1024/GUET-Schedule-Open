package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PrivacyDao {
    @Query("select * from Privacy where privacy_version=:v")
    List<Privacy> selectPrivacy(@NonNull String v);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Privacy tuple);
}
