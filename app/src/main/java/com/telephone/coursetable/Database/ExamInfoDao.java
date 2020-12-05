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

    @Query("update ExamInfo set sts=:sts, ets=:ets where croomno=:room and examdate=:date and kssj=:time")
    void updateSTSAndETS(@NonNull String room, @NonNull String date, @NonNull String time, long sts, long ets);

    @Query("update ExamInfo set sts=:sts where croomno=:room and examdate=:date and kssj=:time")
    void updateSTS(@NonNull String room, @NonNull String date, @NonNull String time, long sts);

    @Query("update ExamInfo set ets=:ets where croomno=:room and examdate=:date and kssj=:time")
    void updateETS(@NonNull String room, @NonNull String date, @NonNull String time, long ets);
}
