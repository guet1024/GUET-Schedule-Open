package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * @clear
 */
@Dao
public interface ExamInfoDao {
    @Query("delete from ExamInfo")
    void deleteAll();

    @Query("select * from ExamInfo order by sts ASC")
    List<ExamInfo> selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExamInfo tuple);

    @Query("select * from ExamInfo where ets>=:nts order by sts ASC")
    List<ExamInfo> selectFromToday(long nts);

    @Query("update ExamInfo set sts=:sts, ets=:ets where sts=:origin_sts and ets=:origin_ets and croomno=:room and courseno=:cno and comm=:comment")
    void updateSTSAndETS(long origin_sts, long origin_ets, @NonNull String room, @NonNull String cno, @NonNull String comment, long sts, long ets);

    @Query("update ExamInfo set sts=:sts where sts=:origin_sts and ets=:origin_ets and croomno=:room and courseno=:cno and comm=:comment")
    void updateSTS(long origin_sts, long origin_ets, @NonNull String room, @NonNull String cno, @NonNull String comment, long sts);

    @Query("update ExamInfo set ets=:ets where sts=:origin_sts and ets=:origin_ets and croomno=:room and courseno=:cno and comm=:comment")
    void updateETS(long origin_sts, long origin_ets, @NonNull String room, @NonNull String cno, @NonNull String comment, long ets);
}
