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
public interface ClassInfoDao {
    @Query("delete from ClassInfo where username=:u")
    void deleteAll(String u);

    @Query("delete from ClassInfo where custom_ref<=0 and username=:u")
    void deleteAllNotReferredByCustom(String u);

    @Query("select * from ClassInfo where username=:u")
    List<ClassInfo> selectAll(String u);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClassInfo tuple);

    @Query("update ClassInfo set custom_ref=0 where username=:u")
    void resetAllCustomRef(String u);

    @Query("update ClassInfo set custom_ref=custom_ref+1 where username=:u and courseno=:cno")
    void increaseCustomRef(String u, String cno);

    @Query("select courseno from ClassInfo where username=:u")
    List<String> selectAllCno(String u);

    @Query("delete from ClassInfo where courseno=:cno and username=:u")
    void deleteCno(String u, String cno);
}
