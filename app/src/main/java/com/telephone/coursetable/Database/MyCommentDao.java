package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * @clear
 */
@Dao
public interface MyCommentDao {
    @Query("delete from MyComment where go_to_class_key_json_string=:key_string")
    void delete(@NonNull String key_string);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MyComment tuple);

    @Query("select my_comment from MyComment where go_to_class_key_json_string=:key_string")
    String getComment(@NonNull String key_string);
}
