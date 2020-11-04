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
public interface CETDao {
    @Query("select * from CET order by term ASC, code ASC")
    List<CET> selectAll();

    @Query("delete from CET")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CET tuple);
}
