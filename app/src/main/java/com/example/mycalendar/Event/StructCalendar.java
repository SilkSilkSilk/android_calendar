package com.example.mycalendar.Event;

import java.util.Calendar;

public class StructCalendar{
    public int id;      //!=null
    public String title;
    public String description;
    public Calendar dtstart;    //!=null
    public Calendar dtend;      //!=null
    public String all_day;
    public String rdate;
    public String rrule;
    public int color;       //!=null

    public StructCalendar clone() {
        StructCalendar structCalendar = new StructCalendar();
        structCalendar.id = this.id;
        structCalendar.title = new String(this.title != null ? this.title : "");
        structCalendar.description = new String(this.description != null ? this.description : "");
        structCalendar.dtstart = (Calendar) this.dtstart.clone();
        structCalendar.dtend = (Calendar) this.dtend.clone();
        structCalendar.all_day = new String(this.all_day != null ? this.all_day : "");
        structCalendar.rdate = new String(this.rdate != null ? this.rdate : "");
        structCalendar.rrule = new String(this.rrule != null ? this.rrule : "");
        structCalendar.color = this.color;
        return structCalendar;
    }
}
