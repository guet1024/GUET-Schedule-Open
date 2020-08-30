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
public interface TermInfoDao {
    @Query("delete from TermInfo")
    void deleteAll();

    @Query("select * from TermInfo")
    List<TermInfo> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TermInfo tuple);

    @Query("delete from TermInfo where term=:term")
    void deleteTerm(String term);

    @Query("select * from TermInfo where sts<=:nts and ets>=:nts")
    List<TermInfo> whichTerm(long nts);

    @Query("select weeknum from TermInfo where termname=:termname")
    List<String> getWeekNumByTermName(String termname);

    @Query("select * from TermInfo where termname=:termname")
    List<TermInfo> getTermByTermName(String termname);

    @Query("select term from TermInfo where term>=:term")
    List<String> getTermsSince(String term);
}
