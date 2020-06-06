package com.example.mycalendar.Event;

import android.graphics.Color;

public class EventInfo {
    int id;
    int color;
    int year;
    int month;
    int day;
    String text;

    public EventInfo(int id, int color, int year, int month, int day, String text) {
        this.id = id;
        this.color = color;
        this.year = year;
        this.month = month;
        this.day = day;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
