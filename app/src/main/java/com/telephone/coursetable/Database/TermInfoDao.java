package com.telephone.coursetable.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

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

    @Query("select * from TermInfo where sts<=:date and ets>=:date")
    List<TermInfo> whichTerm(long date);

    @Query("select weeknum from TermInfo where termname=:termname")
    List<String> getWeekNumByTermName(String termname);

    @Query("select term from TermInfo where termname=:termname")
    List<String> getTermCodeByTermName(String termname);
}
