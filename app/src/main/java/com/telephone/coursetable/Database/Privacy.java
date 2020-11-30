package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Privacy {
    @NonNull
    @PrimaryKey
    public String privacy_version;

    public Privacy(@NonNull String privacy_version) {
        this.privacy_version = privacy_version;
    }
}
