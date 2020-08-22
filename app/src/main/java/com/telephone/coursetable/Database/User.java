package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @clear
 */
@Entity(primaryKeys = {"username"})
public class User {
    @NonNull
    public String username;

    @NonNull
    public String password;

    @NonNull
    public String aaw_password;

    @NonNull
    public String vpn_password;

    @NonNull
    public boolean activated;

    @NonNull
    public String updateTime;

    public User(@NonNull String username, @NonNull String password, @NonNull String aaw_password, @NonNull String vpn_password){
        if (username == null){
            username = "";
        }
        if (password == null){
            password = "";
        }
        if (aaw_password == null){
            aaw_password = "";
        }
        if (vpn_password == null){
            vpn_password = "";
        }
        this.password = password;
        this.username = username;
        this.aaw_password = aaw_password;
        this.vpn_password = vpn_password;
        this.activated = false;
        this.updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.M.d H:mm:ss"));
    }
}
