package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Version {
    @PrimaryKey
    @NonNull
    public String version_name;

    public Version(@NonNull String version_name) {
        this.version_name = version_name;
    }
}
