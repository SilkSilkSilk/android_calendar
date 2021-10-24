package com.example.mycalendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycalendar.AddView.AddActivity;
import com.example.mycalendar.Event.CalendarApi;
import com.example.mycalendar.Event.CalendarRRule;
import com.example.mycalendar.Event.EventAdapter;
import com.example.mycalendar.Event.EventInfo;
import com.example.mycalendar.Event.StructCalendar;
import com.example.mycalendar.SettingView.SettingActivity;
import com.example.mycalendar.base.activity.BaseActivity;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.TrunkBranchAnnals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements
    CalendarView.OnCalendarSelectListener,
    CalendarView.OnCalendarLongClickListener,
    CalendarView.OnMonthChangeListener,
    CalendarView.OnYearChangeListener,
    CalendarView.OnWeekChangeListener,
    CalendarView.OnViewChangeListener,
    CalendarView.OnYearViewChangeListener {

    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;
    RecyclerView recyclerView;
    EventAdapter eventAdapter;
    List<EventInfo> recycler_list = new ArrayList<>();
    CalendarApi calendarApi;
    int my_count_id;
    List<StructCalendar> all_event;
    List<StructCalendar> calendarView_event = new ArrayList<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 1:
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "You have the permission", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "NewApi"})
    @Override
    protected void initView() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.READ_CALENDAR }, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_CALENDAR }, 2);
        }
        setStatusBarDarkMode();
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        //mCalendarView.setRange(2018, 7, 1, 2019, 4, 28);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.main_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.main_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarLongClickListener(this, true);
        mCalendarView.setOnWeekChangeListener(this);
        mCalendarView.setOnYearViewChangeListener(this);
        //设置日期拦截事件，仅适用单选模式，当前无效
        //mCalendarView.setOnCalendarInterceptListener(this);
        mCalendarView.setOnViewChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void initData() {
        //后面读取本地文件和自定义文件，增加按钮搜索本机事件和配置文件事件，将这些事件应用到日历上
        //读取数据库文件，发现本地的颜色配置
        my_count_id = CalendarApi.checkAndAddCalendarAccount(this);
        all_event = CalendarApi.readAllAccountEvent(this);
        if (all_event.size() == 0)
        {
            Log.i("error", "readAccountEvent error");
        }
//        boolean flag = false;
//        for (structCalendar item : all_event) {
//            if (item.title.equals("test")) {
//                flag = true;
//                break;
//            }
//        }
//        if (flag) {
//            CalendarApi.deleteCalendarEvent(this, "test");
//        }
//        List<Integer> list = new ArrayList<Integer>();
//        list.add(0);
//        list.add(5);
//        list.add(10);
//        java.util.Calendar beginTime = java.util.Calendar.getInstance();
//        beginTime.set(2020, 3 - 1, 25, 10, 0, 0);   //因为月份从0开始，所以用month - 1
//        java.util.Calendar endTime = java.util.Calendar.getInstance();
//        endTime.set(2020, 3 - 1, 25, 15, 0, 0);     //因为月份从0开始，所以用month - 1
//        boolean ret = CalendarApi.addCalendarEvent(this, my_count_id, "test", "", false, beginTime, endTime,
//                "", "", list, getResources().getColor(R.color.color1));
//        if (ret)
//        {
//            Toast.makeText(this, "addCalendarEvent success", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Toast.makeText(this, "addCalendarEvent fault", Toast.LENGTH_SHORT).show();
//        }

        if(false)   //是否有配置文件
        {

        }
        else        //default设置
        {
            refreshMonthView(mCalendarView.getCurYear(), mCalendarView.getCurMonth());
        }
        //筛选今日事件给recyclerView显示
        getCurDateEventInfo(calendarView_event, recycler_list, mCalendarView.getCalendar(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay()));
        eventAdapter = new EventAdapter(recycler_list);
        recyclerView.setAdapter(eventAdapter);

//        CalendarRRule.RRule_parse("FREQ=WEEKLY;INTERVAL=2");
//        java.util.Calendar calendar = java.util.Calendar.getInstance();
//        calendar.set(java.util.Calendar.MONTH, 2);
//        CalendarRRule.RRule_parse_mouth("FREQ=WEEKLY;UNTIL=20240409T080000Z;WKST=SU;BYDAY=TH", calendar, 2020, 4);
    }

    /**
     * 在CalendarView上刷新当前月份的事件高亮
     * @param year 当前年
     * @param month   当前月
     */
    private void refreshMonthView(int year, int month) {
        Map<String, Calendar> map = new HashMap<>();
        calendarView_event.clear();
        java.util.Calendar util_calendar = java.util.Calendar.getInstance();
        util_calendar.set(java.util.Calendar.YEAR, year);
        util_calendar.set(java.util.Calendar.MONTH, month - 1);
        final int day = util_calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        Log.i("--getActualMaximum--: ", String.valueOf(day));
        Calendar calendar;
        for(StructCalendar item : all_event) {
            if (item.rrule != null){
                List<java.util.Calendar> list = CalendarRRule.RRule_parse_mouth(item.rrule, item.dtstart, year, month);
                if (list != null && list.size() > 0){
                    for(java.util.Calendar cld : list) {
                        if (cld.get(java.util.Calendar.YEAR) == year && (cld.get(java.util.Calendar.MONTH) + 1 == month)) {    //因为java.util.Calendar.MONTH的月份从0开始，所以这里get数据要用month + 1
                            calendar = getSchemeCalendar(year, month, cld.get(java.util.Calendar.DATE), item.color, item.title != null ? item.title.substring(0, 1) : "");
                            map.put(calendar.toString(), calendar); //这里的map可能会覆盖，因为toString()是重构的，是年月日的字符串；后面需要读取事件的显示优先级

                            StructCalendar structCalendar = item.clone();
                            structCalendar.dtstart.setTimeInMillis(cld.getTimeInMillis());
                            calendarView_event.add(structCalendar);
                        }
                    }
                }
            } else if (item.dtstart.get(java.util.Calendar.YEAR) == year && (item.dtstart.get(java.util.Calendar.MONTH) + 1 == month)) {    //因为java.util.Calendar.MONTH的月份从0开始，所以这里get数据要用month + 1
                calendar = getSchemeCalendar(year, month, item.dtstart.get(java.util.Calendar.DATE), item.color, item.title != null && !item.title.isEmpty() ? item.title.substring(0, 1) : "");
                map.put(calendar.toString(), calendar); //这里的map可能会覆盖，因为toString()是重构的，是年月日的字符串；后面需要读取事件的显示优先级

                StructCalendar structCalendar = item.clone();
                calendarView_event.add(structCalendar);
            }
        }
        //这里可以获取节假日，然后制作高亮
//            calendar = mCalendarView.getCalendar(2020, 3,12);
//            Log.i("--retDateInfo--: ", getCalendarText(calendar != null ? calendar : new Calendar()));
        mCalendarView.setSchemeDate(map);
    }

    /**
     * 筛选今日事件给recyclerView显示
     * @param src_list event对象
     * @param list   recycler list对象
     * @param calendar   需要筛选的日期对象
     */
    private void getCurDateEventInfo(List<StructCalendar> src_list, List<EventInfo> list, Calendar calendar) {
        Log.i("getCurDateEventInfo", calendar.getYear() + " " + calendar.getMonth() + " " + calendar.getDay());
        list.clear();
        for(StructCalendar item : src_list)
        {
            if (item.dtstart.get(java.util.Calendar.YEAR) == calendar.getYear() && (item.dtstart.get(java.util.Calendar.MONTH) + 1 == calendar.getMonth()) &&
                    item.dtstart.get(java.util.Calendar.DATE) == calendar.getDay())     //因为java.util.Calendar.MONTH的月份从0开始，所以这里get数据要用month + 1
            {
                EventInfo event = new EventInfo(item.id, item.color, calendar.getYear(), calendar.getMonth(), calendar.getDay(), item.title);
                //这里先忽略掉item.rrule的规则
                list.add(event);
            }
        }
    }


    /**
     * 打印当前日期对象的信息
     * @param calendar 日期对象
     */
    private static String getCalendarText(Calendar calendar) {
        return String.format("新历%s \n 农历%s \n 公历节日：%s \n 农历节日：%s \n 节气：%s \n 是否闰月：%s",
                calendar.getMonth() + "月" + calendar.getDay() + "日",
                calendar.getLunarCalendar().getMonth() + "月" + calendar.getLunarCalendar().getDay() + "日",
                TextUtils.isEmpty(calendar.getGregorianFestival()) ? "无" : calendar.getGregorianFestival(),
                TextUtils.isEmpty(calendar.getTraditionFestival()) ? "无" : calendar.getTraditionFestival(),
                TextUtils.isEmpty(calendar.getSolarTerm()) ? "无" : calendar.getSolarTerm(),
                calendar.getLeapMonth() == 0 ? "否" : String.format("闰%s月", calendar.getLeapMonth()));
    }


    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        Toast.makeText(this, "长按不选择日期\n" + getCalendarText(calendar), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : LongClickOutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }


    /**
     * nCalendarView的回调函数，改变日期时会调用，包括了月视图和年视图切换
     * @param calendar 日期对象
     * @param isClick 是否是手指点击切换的日期
     */
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        if (calendar == null){
            return;
        }
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        if (isClick) {
            Toast.makeText(this, getCalendarText(calendar), Toast.LENGTH_SHORT).show();
        }
        getCurDateEventInfo(calendarView_event, recycler_list, calendar);    //筛选今日事件给recyclerView显示
        eventAdapter.notifyDataSetChanged();
        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
        Log.e("onDateSelected", "  " + mCalendarView.getSelectedCalendar().getScheme() +
                "  --  " + mCalendarView.getSelectedCalendar().isCurrentDay());
        Log.e("干支年纪 ： ", " -- " + TrunkBranchAnnals.getTrunkBranchYear(calendar.getLunarCalendar().getYear()));
    }


    /**
     * nCalendarView的回调函数，月视图切换时调用
     * @param year 日期对象
     * @param month 日期对象
     */
    @Override
    public void onMonthChange(int year, int month) {
        Log.e("onMonthChange", "  -- " + year + "  --  " + month);
        Calendar calendar = mCalendarView.getSelectedCalendar();
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();

        refreshMonthView(year, month);  //在CalendarView上刷新当前月份的事件高亮
        //这里的calendar数据可能会停留在上个月，所以list更新不放在这
    }

    @Override
    public void onViewChange(boolean isMonthView) {
        Log.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
    }

    @Override
    public void onWeekChange(List<Calendar> weekCalendars) {
        for (Calendar calendar : weekCalendars) {
            Log.e("onWeekChange", calendar.toString());
        }
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
        Log.e("onYearChange", " 年份变化 " + year);
    }

    @Override
    public void onYearViewChange(boolean isClose) {
        Log.e("onYearViewChange", "年视图 -- " + (isClose ? "关闭" : "打开"));
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK)
                {
                    //add界面追加event后
                    all_event = CalendarApi.readAllAccountEvent(MainActivity.this); //重新读取事件信息
                    if (all_event.size() == 0)
                    {
                        Log.i("error", "readAccountEvent error");
                    }
                    refreshMonthView(mCalendarView.getCurYear(), mCalendarView.getCurMonth());  //在CalendarView上刷新当前月份的事件高亮
                    getCurDateEventInfo(calendarView_event, recycler_list, mCalendarView.getSelectedCalendar());    //筛选今日事件给recyclerView显示
                    eventAdapter.notifyDataSetChanged();
                }
                break;
            default:
        }
    }
}
