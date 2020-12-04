package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MaskDao {
    @Query("select mask from Mask where sid=:sid")
    List<String> getMask(@NonNull String sid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Mask tuple);
}
