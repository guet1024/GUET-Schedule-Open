package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * @clear
 */
@Entity(primaryKeys = {"go_to_class_key_json_string"})
public class MyComment {
    @NonNull
    public String go_to_class_key_json_string;
    @NonNull
    public String my_comment;

    public MyComment(@NonNull String go_to_class_key_json_string, @NonNull String my_comment) {
        this.go_to_class_key_json_string = go_to_class_key_json_string;
        this.my_comment = my_comment;
    }
}
