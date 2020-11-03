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
public interface LABDao {
    @Query("select * from LAB")
    List<LAB> selectAll();

    @Query("delete from LAB")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LAB tuple);
}
