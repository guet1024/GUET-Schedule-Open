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

    /**
     * this method will return a course list of specified period
     */
    @Query("select g.courseno, c.cname, c.name, g.croomno from " +
                "(select courseno, croomno from GoToClass where term=:term and startweek<=:week and endweek>=:week and weekday=:weekday and time=:time) g, " +
                "(select courseno, cname, name from ClassInfo) c " +
            "where g.courseno=c.courseno ")
    List<ShowTableNode> getNode(String term, long week, long weekday, String time);

    /**
     * this method will return a course list of specified week
     */
    @Query("select g.courseno, c.cname, c.name, g.croomno from " +
                "(select courseno, croomno, time, weekday from GoToClass where term=:term and startweek<=:week and endweek>=:week) g, " +
                "(select courseno, cname, name from ClassInfo) c " +
            "where g.courseno=c.courseno " +
            "order by g.time ASC, g.weekday ASC ")
    List<ShowTableNode> getSpecifiedWeekTable(String term, long week);
}
