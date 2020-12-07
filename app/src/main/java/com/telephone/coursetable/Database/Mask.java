package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Mask {
    @NonNull
    @PrimaryKey
    public String sid;
    @NonNull
    public String mask;

    public Mask(@NonNull String sid, @NonNull String mask) {
        this.sid = sid;
        this.mask = mask;
    }
}
