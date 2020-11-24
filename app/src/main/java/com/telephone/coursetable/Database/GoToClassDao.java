package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.telephone.coursetable.Database.KeyAndValue.GoToClassKeyAndValue;

import java.util.List;

/**
 * @clear
 */
@Dao
public interface GoToClassDao {
    @Query("delete from GoToClass where username=:u")
    void deleteAll(String u);

    @Query("delete from GoToClass where customized=0 and username=:u")
    void deleteAllNotCustomized(String u);

    @Query("select * from GoToClass where username=:u")
    List<GoToClass> selectAll(String u);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GoToClass tuple);

    /**
     * this method will return a {@link ShowTableNode} list according to specified period
     */
    @Query("select g.courseno courseno, c.cname cname, c.name name, g.croomno croomno, g.weekday weekday, g.time time, g.startweek start_week, g.endweek end_week, c.teacherno tno, g.sys_comm sys_comm, g.my_comm my_comm, g.customized customized, g.oddweek oddweek, c.xf grade_point, c.tname ctype, c.examt examt from " +
                "(select courseno, croomno, weekday, time, startweek, endweek, sys_comm, my_comm, customized, oddweek from GoToClass where term=:term and startweek<=:week and endweek>=:week and weekday=:weekday and time=:time and username=:u) g, " +
                "(select courseno, cname, name, teacherno, xf, tname, examt from ClassInfo where username=:u) c " +
            "where g.courseno=c.courseno ")
    List<ShowTableNode> getNode(String u, String term, long week, long weekday, String time);

    /**
     * this method will return a {@link ShowTableNode} list according to specified week
     */
    @Query("select g.courseno courseno, c.cname cname, c.name name, g.croomno croomno, g.weekday weekday, g.time time, g.startweek start_week, g.endweek end_week, c.teacherno tno, g.sys_comm sys_comm, g.my_comm my_comm, g.customized customized, g.oddweek oddweek, c.xf grade_point, c.tname ctype, c.examt examt from " +
                "(select courseno, croomno, time, weekday, startweek, endweek, sys_comm, my_comm, customized, oddweek from GoToClass where term=:term and startweek<=:week and endweek>=:week and username=:u) g, " +
                "(select courseno, cname, name, teacherno, xf, tname, examt from ClassInfo where username=:u) c " +
            "where g.courseno=c.courseno " +
            "order by g.time ASC, g.weekday ASC ")
    List<ShowTableNode> getSpecifiedWeekTable(String u, String term, long week);

    /**
     * this method will return a {@link ShowTableNode} list according to specified day
     */
    @Query("select g.courseno courseno, c.cname cname, c.name name, g.croomno croomno, g.weekday weekday, g.time time, g.startweek start_week, g.endweek end_week, c.teacherno tno, g.sys_comm sys_comm, g.my_comm my_comm, g.customized customized, g.oddweek oddweek, c.xf grade_point, c.tname ctype, c.examt examt from " +
            "(select courseno, croomno, weekday, time, startweek, endweek, sys_comm, my_comm, customized, oddweek from GoToClass where term=:term and startweek<=:week and endweek>=:week and weekday=:weekday and username=:u) g, " +
            "(select courseno, cname, name, teacherno, xf, tname, examt from ClassInfo where username=:u) c " +
            "where g.courseno=c.courseno ")
    List<ShowTableNode> getTodayLessons(String u, String term, long week, long weekday);

    @Query("update GoToClass set sys_comm=null where username=:u")
    void clearAllSysComment(String u);

    @Query("select courseno from GoToClass where username=:u and customized=1")
    List<String> selectCustomCno(String u);

    @Query("select courseno from GoToClass where username=:u")
    List<String> selectAllCno(String u);

    @Query("select username, term, weekday, time, courseno, startweek, endweek, oddweek, my_comm from GoToClass")
    List<GoToClassKeyAndValue> getMyCommentPairs_all_users();

    @Query("update GoToClass set my_comm=:my_comm where username=:u and term=:term and weekday=:weekday and time=:time and courseno=:courseno and startweek=:startweek and endweek=:endweek and oddweek=:oddweek")
    void setMyComment(String u, String term, long weekday, String time, String courseno, long startweek, long endweek, boolean oddweek, String my_comm);
}
