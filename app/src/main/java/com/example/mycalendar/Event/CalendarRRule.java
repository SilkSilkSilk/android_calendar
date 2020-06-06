package com.example.mycalendar.Event;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import biweekly.Biweekly;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.ParseContext;
import biweekly.io.scribe.property.RecurrenceRuleScribe;
import biweekly.parameter.ICalParameters;
import biweekly.property.RecurrenceRule;
import biweekly.util.Recurrence;
import biweekly.util.com.google.ical.compat.javautil.DateIterator;

public class CalendarRRule {
    /**
     * 解析calendar的rule，为期1个月
     * @param src_rule rrule
     * @param cld_start 事件开始时间
     * @param year   目标年 0~
     * @param mouth  目标月 1~12
     */
    public static List<java.util.Calendar> RRule_parse_mouth(String src_rule, Calendar cld_start, int year, int mouth)
    {
        List<java.util.Calendar> list = new ArrayList<>();
        if (src_rule == null)
            return list;
        Log.i("RRule_parse_mouth", "RRULE: " + src_rule);
        if (cld_start == null || cld_start.get(Calendar.YEAR) > year || (cld_start.get(Calendar.YEAR) == year && cld_start.get(Calendar.MONTH) > mouth)) {   //事件开始时间在目标时间之后
            return list;
        }
        RecurrenceRuleScribe scribe = new RecurrenceRuleScribe();
        ParseContext context = new ParseContext();
        context.setVersion(ICalVersion.V2_0);
        RecurrenceRule rrule = scribe.parseText(src_rule,null, new ICalParameters(), context);
        Recurrence recur = rrule.getValue();
        Date until_date = recur.getUntil();
        if (until_date != null) {
            Log.i("RRule_parse_mouth", "UNTIL: " + (until_date.getYear() + 1900) + "/" + until_date.getMonth());
            if (until_date.getYear() + 1900 < year || (until_date.getYear() + 1900 == year && until_date.getMonth() < mouth)) {  //事件结束时间在目标时间之前
                return list;
            }
        }
        java.util.Calendar calendar_start = (Calendar) cld_start.clone();
        calendar_start.set(Calendar.YEAR, year);
        calendar_start.set(Calendar.MONTH, mouth - 1);
        calendar_start.set(Calendar.DAY_OF_MONTH, 1);
        java.util.Calendar calendar_end = (Calendar) calendar_start.clone();
        calendar_end.set(Calendar.DAY_OF_MONTH, calendar_end.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        TimeZone timezone = TimeZone.getDefault();
        DateIterator it = rrule.getDateIterator(new Date(cld_start.getTimeInMillis()), timezone);       //这里的起始时间在UNTIL的规则明确的情况下是从起始时间开始解析，解析之后的时分秒是当前时间，不过时分秒并不需要
                                                                                                    //农历解析不了，只会解析公历...
        while (it.hasNext()) {                  //先顺序遍历，以后再优化
            Date date = it.next();
            if (date.getTime() >= calendar_start.getTimeInMillis() && date.getTime() <= calendar_end.getTimeInMillis()) {           //确保在目标月之内
                Log.i("RRule_parse_mouth", date.toString());
                Calendar time = Calendar.getInstance();
                time.setTimeInMillis(date.getTime());
                list.add(time);
            } else if (date.getTime() > calendar_end.getTimeInMillis()){
                break;
            }
        }
        return list;
    }
}
