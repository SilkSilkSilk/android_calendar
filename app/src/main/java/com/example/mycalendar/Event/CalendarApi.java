package com.example.mycalendar.Event;


import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CalendarApi {
    public static String CALENDARS_NAME = "mycalendar";
    public static String CALENDARS_ACCOUNT_NAME = "mycalendar@mycalendar.com";
    public static String CALENDARS_ACCOUNT_TYPE = "com.android.mycalendar";
    public static String CALENDARS_DISPLAY_NAME = "mycalendar账户";

    public static List<AccountInfo> ScanAccount(Context context)
    {
        List<AccountInfo> ret = new ArrayList<>();
        @SuppressLint("MissingPermission") Cursor userCursor = context.getContentResolver().query(Calendars.CONTENT_URI, null, null, null, null);
        if (userCursor == null || userCursor.getCount() <= 0)
            return ret;
        for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
            String name = userCursor.getString(userCursor.getColumnIndex(Calendars.NAME));
            String accountmame = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
            String useridstr = userCursor.getString(userCursor.getColumnIndex(Calendars._ID));
            long userid = useridstr != null ? Long.parseLong(useridstr) : 0;
            ret.add(new AccountInfo(name, accountmame, (int)userid));
        }
        return ret;
    }

    public static int CheckAccount(Context context, String username)
    {
        int id = 0;
        @SuppressLint("MissingPermission") Cursor userCursor = context.getContentResolver().query(Calendars.CONTENT_URI, null, null, null, null);
        try {
            if (userCursor == null || userCursor.getCount() <= 0)
                return -1;
            Log.i("Count: ", String.valueOf(userCursor.getCount()));
            boolean flag = false;
            for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
                String userName1 = userCursor.getString(userCursor.getColumnIndex(Calendars.NAME));
                String userName0 = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
                String useridstr = userCursor.getString(userCursor.getColumnIndex(Calendars._ID));
                long userid = useridstr != null ? Long.parseLong(useridstr) : 0;
                Log.i("name: ", "NAME: " + userName1 + " -- ACCOUNT_NAME: " + userName0 + " -- ACCOUNT_ID: " + useridstr);
                if (userName1 != null && !userName1.isEmpty() && userName1.equals(username)) {
                    flag = true;
                    Log.i("success: ", "find " + CALENDARS_NAME);
                    id = (int)userid;
                }
            }
            if (flag == false) {
                Log.i("error: ", "not find " + CALENDARS_NAME);
                return -1;
            } else {
                return id;
            }
        }
        finally {
            if (userCursor != null)
            {
                userCursor.close();
            }
        }
    }

    public static long addCalendarAccount(Context context)
    {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, CALENDARS_NAME);
        value.put(Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    /**
     * 删除日历账户
     */
    public static void delCalendarAccount(Context context, long id) {
        if (context == null) {
            return;
        }
        @SuppressLint("MissingPermission") Cursor userCursor = context.getContentResolver().query(Calendars.CONTENT_URI, null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return;
            }
            if (userCursor.getCount() > 0) {
                for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
                    String useridstr = userCursor.getString(userCursor.getColumnIndex(Calendars._ID));
                    long userid = useridstr != null ? Long.parseLong(useridstr) : 0;
                    if (userid != 0 && userid == id) {
                        Uri deleteUri = ContentUris.withAppendedId(Calendars.CONTENT_URI, id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) { //账户删除失败
                            Log.i("error: ", "Account" + id + "delete failure");
                            return;
                        } else {
                            Log.i("error: ", "Account" + id + "delete success");
                        }
                    }
                }
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    public static int checkAndAddCalendarAccount(Context context) {
        int issuccess = CheckAccount(context, CALENDARS_NAME);
        if( issuccess == -1 ){
            return (int)addCalendarAccount(context);
        }else{
            if (issuccess >= 0) {
                Log.i("success: ", "find " + CALENDARS_NAME + "   account id = " + issuccess);
                return issuccess;
            } else {
                return -1;
            }
        }
    }


    /**
     * 获取账户的日历事件，这里是能获取到所有的账号事件的，CalendarContract.Events.CALENDAR_ID就是账号id
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<StructCalendar> readAllAccountEvent(Context context)
    {
        @SuppressLint("MissingPermission") Cursor eventCursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
        try {
            if (eventCursor == null || eventCursor.getCount() <= 0)
                return new ArrayList<>();
            Log.i("eventCount: ", String.valueOf(eventCursor.getCount()));
            List<StructCalendar> list = new ArrayList<>();
            for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                StructCalendar item = new StructCalendar();
                String idstr = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
                item.id = idstr != null ? Integer.parseInt(idstr) : 0;
                item.title = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.TITLE));
                item.description = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                String dtstart = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.DTSTART));
                String dtend = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.DTEND));
                long starttime = dtstart != null ? Long.parseLong(dtstart) : 0;
                long endtime = dtend != null ? Long.parseLong(dtend) : 0;
                item.dtstart = Calendar.getInstance();
                item.dtstart.setTimeInMillis(starttime);
                item.dtend = Calendar.getInstance();
                item.dtend.setTimeInMillis(endtime);
                item.all_day = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.ALL_DAY));
                item.rdate = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.RDATE));
                item.rrule = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.RRULE));
                String colorstr = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.EVENT_COLOR));
                item.color =  colorstr != null ? Integer.parseInt(colorstr) : 0; //目前string转color不知道怎么弄
                list.add(item);
                Log.i("Account", item.id + " -- title: " + item.title + " -- description: " + item.description + " -- starttime: " + item.dtstart.getTime().toString()
                        + " -- endtime: " + item.dtend.getTime().toString() + " -- all_day: " + item.all_day + " -- rdate: " + item.rdate + " -- rrule: " + item.rrule + " -- color: " + colorstr);
            }
            return list;
        }
        finally {
            if (eventCursor != null)
            {
                eventCursor.close();
            }
        }
    }


    /**
     * 添加日历事件
     * @param title 标题
     * @param description   详细描述->目前是记录农历提醒
     * @param all_day   全天模式
     * @param startTime   事件开始时间
     * @param endTime   事件结束时间
     * @param rdate   	事件重复的日期
     * @param rrule   事件重复规则 （一次、每天、每周、每两周、每月、每年、每周一~日）
     * @param previousTime   具体提醒时间（提前一天或者5分钟 按minutes为单位）
     * @param color  颜色
     * @return boolean 成功执行返回true
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean addCalendarEvent(Context context, int calId, String title, String description, boolean all_day, Calendar startTime, Calendar endTime, String rdate, String rrule, List<Integer> previousTime, int color) {
        if (context == null || calId < 0) {
            Log.i("error: ", "not find " + calId);
            return false;
        }
        //添加日历事件
        long start = startTime.getTimeInMillis();
        long end = endTime.getTimeInMillis();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.DESCRIPTION, description);
        event.put(CalendarContract.Events.CALENDAR_ID, calId);
        if (all_day) {
            event.put(CalendarContract.Events.ALL_DAY, true);
        }
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM,  1);//设置有闹钟提醒
        event.put(CalendarContract.Events.RDATE, rdate);
        event.put(CalendarContract.Events.RRULE, rrule);
        event.put(CalendarContract.Events.EVENT_COLOR, String.valueOf(color));   //color
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
        @SuppressLint("MissingPermission") Uri newEvent = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event); //添加事件
        if (newEvent == null) { //添加日历事件失败直接返回
            Log.i("error: ", "newEvent == null");
            return false;
        }
        //事件提醒的设定
        if (!previousTime.isEmpty()){
            for(int i : previousTime){
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
                values.put(CalendarContract.Reminders.MINUTES, i);// 提前previousDate天有提醒
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                @SuppressLint("MissingPermission") Uri uri = context.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);
                if(uri == null) { //添加事件提醒失败直接返回
                    Log.i("error: ", "添加事件提醒失败直接返回");
                    return false;
                }
            }
        }
        Log.i("success: ", "添加事件成功");
        return true;
    }


    /**
     * 删除日历事件
     */
    public static void deleteCalendarEvent(Context context, String title) {
        if (context == null) {
            return;
        }
        @SuppressLint("MissingPermission") Cursor eventCursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                Log.i("error: ", "事件查询失败");
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) { //事件删除失败
                            Log.i("error: ", "事件删除失败");
                            return;
                        }else{
                            Log.i("success: ", "事件删除成功" + id);
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }
}
