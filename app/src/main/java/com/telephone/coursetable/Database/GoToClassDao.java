package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GoToClassDao {
    @Query("delete from GoToClass")
    void deleteAll();

    @Query("select * from GoToClass")
    List<GoToClass> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GoToClass tuple);

    @Query("select g.courseno, c.cname, c.name, g.croomno from " +
                "(select courseno, croomno from GoToClass where term=:term and weekday=:weekday and time=:time) g, " +
                "(select courseno, cname, name from ClassInfo) c " +
            "where g.courseno=c.courseno ")
    List<ShowTableNode> getNodes(String term, long weekday, String time);
}
