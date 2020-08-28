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
public interface GradesDao {

    @Query("select * from Grades order by term DESC, cname ASC")
    List<Grades> selectAll();

    @Query("delete from Grades")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Grades tuple);
}
