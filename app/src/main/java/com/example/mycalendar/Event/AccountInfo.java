package com.example.mycalendar.Event;

public class AccountInfo{
    String name;
    String account_name;
    int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AccountInfo(String name, String account_name, int id) {
        this.name = name;
        this.account_name = account_name;
        this.id = id;
    }
}
