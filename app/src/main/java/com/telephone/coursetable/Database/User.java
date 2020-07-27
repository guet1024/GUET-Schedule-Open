package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(primaryKeys = {"username"})
public class User {
    @NonNull
    public String username;

    @NonNull
    public String password;

    @NonNull
    public boolean activated;

    @NonNull
    public String updateTime;

    public User(String username, String password){
        if (username == null){
            username = "";
        }
        if (password == null){
            password = "";
        }
        this.password = password;
        this.username = username;
        activated = false;
        updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.M.d H:m:s"));
    }
}
