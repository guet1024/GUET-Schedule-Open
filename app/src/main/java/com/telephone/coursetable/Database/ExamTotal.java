package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ExamTotal {
    @NonNull
    @PrimaryKey
    public String total;
    public boolean read = false;

    public ExamTotal(@NonNull String total) {
        this.total = total;
    }
}
