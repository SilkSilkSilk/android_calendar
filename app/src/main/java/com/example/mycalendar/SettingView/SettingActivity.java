package com.example.mycalendar.SettingView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.mycalendar.R;
import com.example.mycalendar.base.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingActivity extends BaseActivity {
    Button b_account;
    Button b_color;
    ListView listView;
    String list_data[];

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        setStatusBarDarkMode();
        b_account = findViewById(R.id.setting_account);
        b_color = findViewById(R.id.setting_color);
        listView = findViewById(R.id.setting_list);

        b_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        b_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        list_data = getResources().getStringArray(R.array.list_dialog_items);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list_data);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SettingActivity.this, "已设置：" + list_data[position], Toast.LENGTH_SHORT).show();
                switch (position) {
//                    case 0:
//                        mCalendarView.setWeekStarWithSun();
//                        break;
//                    case 1:
//                        mCalendarView.setWeekStarWithMon();
//                        break;
//                    case 2:
//                        mCalendarView.setWeekStarWithSat();
//                        break;
//                    case 3:
//                        if (mCalendarView.isSingleSelectMode()) {
//                            mCalendarView.setSelectDefaultMode();
//                        } else {
//                            mCalendarView.setSelectSingleMode();
//                        }
//                        break;
//                    case 4:
//                        mCalendarView.setWeekView(MeizuWeekView.class);
//                        mCalendarView.setMonthView(MeiZuMonthView.class);
//                        mCalendarView.setWeekBar(EnglishWeekBar.class);
//                        break;
//                    case 5:
//                        mCalendarView.setAllMode();
//                        break;
//                    case 6:
//                        mCalendarView.setOnlyCurrentMode();
//                        break;
//                    case 7:
//                        mCalendarView.setFixMode();
//                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {

    }
}
