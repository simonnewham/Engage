package com.engage.simonnewham.engageapp.models;

import java.util.ArrayList;

/**
 * Created by simonnewham on 2018/07/31.
 */

public class User {

    private String email;
    private String password;
    private String user_group;

    public User(String email, String password, String user_group) {
        this.email = email;
        this.password = password;
        this.user_group = user_group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_group() {
        return user_group;
    }

    public void setUser_group(String user_group) {
        this.user_group = user_group;
    }

}
