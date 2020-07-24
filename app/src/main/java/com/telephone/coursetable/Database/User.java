package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"username"})
public class User {
    @NonNull
    public String username;

    @NonNull
    public String password;

    public User(String username, String password){
        if (username == null){
            username = "";
        }
        if (password == null){
            password = "";
        }
        this.password = password;
        this.username = username;
    }
}
