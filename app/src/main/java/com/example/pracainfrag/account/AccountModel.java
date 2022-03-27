package com.example.pracainfrag.account;

import java.io.Serializable;

public class AccountModel implements Serializable {

    String login;
    String name;
    String email;
    String phone;
    String user_id;
    boolean is_driver;

    public AccountModel(){

    }

    public AccountModel(String user_id, String login, String name, String email, String phone, boolean is_driver){
        this.user_id = user_id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.is_driver = is_driver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhoneNumber(String phone) { this.phone = phone; }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUser_id() {
        return user_id;
    }

    public boolean getIs_driver(){return is_driver;}

    public void setUser_id(String user_id) {this.user_id = user_id;}

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIs_driver(boolean isDriver) { this.is_driver = isDriver; }

}
