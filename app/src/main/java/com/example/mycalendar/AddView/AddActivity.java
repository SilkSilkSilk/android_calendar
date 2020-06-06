package com.example.mycalendar.AddView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mycalendar.Event.AccountInfo;
import com.example.mycalendar.Event.CalendarApi;
import com.example.mycalendar.MainActivity;
import com.example.mycalendar.R;
import com.example.mycalendar.base.activity.BaseActivity;
import com.shehuan.niv.NiceImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class MHashSet<E> extends HashSet<E>{
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj.getClass() == Integer.class) {
            Integer integer = (Integer)obj;
            return this.equals(integer);
        }
        return false;
    }
}

interface ImgButtonListener{
    /*
    flag : on -> checked
     */
    void OnClick(boolean flag);
}

public class AddActivity extends BaseActivity {
    ImageButton ib_all_day;
    ImageButton ib_lunar;
    boolean ib_all_day_flag = false;    //是否按下
    boolean ib_lunar_flag = false;
    ImgButtonListener imgButtonListener_all_day;
    ImgButtonListener imgButtonListener_lunar;
    Button b_start;
    java.util.Calendar calendar_start;
    TimePickerDialog.OnTimeSetListener onTimeSetListener_start;
    Button b_end;
    java.util.Calendar calendar_end;
    TimePickerDialog.OnTimeSetListener onTimeSetListener_end;
    Button b_color;
    ColorDialog.ColorDialogSetInfo colorDialogSetInfo;
    NiceImageView niv_color;
    int color = 0;
    Button b_repeat;
    int checkedItem_repeat = 0;
    DialogInterface.OnDismissListener onDismissListener_repeat;
    Button b_remind;
    MHashSet<Integer> checkedItem_remind = new MHashSet<>();
    DialogInterface.OnDismissListener onDismissListener_remind;
    Button b_account;
    int checkedItem_account = 0;
    DialogInterface.OnDismissListener onDismissListener_account;
    List<AccountInfo> accountInfoList;
    Button button_ok;
    Button button_cancel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add;
    }

    @Override
    protected void initView() {
        setStatusBarDarkMode();
        ib_all_day_flag = false;
        ib_lunar_flag = false;
        ib_all_day = findViewById(R.id.add_all_day);
        ib_all_day.setBackground(getResources().getDrawable(R.drawable.ic_switch_off));
        ib_lunar = findViewById(R.id.add_lunar);
        ib_lunar.setBackground(getResources().getDrawable(R.drawable.ic_switch_off));
        b_start = findViewById(R.id.add_start);
        b_end = findViewById(R.id.add_end);
        b_color = findViewById(R.id.add_color);
        niv_color = findViewById(R.id.add_color_image);
        b_repeat = findViewById(R.id.add_repeat);
        b_remind = findViewById(R.id.add_remind);
        b_account = findViewById(R.id.add_account);
        button_ok = findViewById(R.id.add_ok);
        button_cancel = findViewById(R.id.add_cancel);

        ib_all_day.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if (ib_all_day_flag == false)
                    {
                        ((ImageButton)v).setBackground(getResources().getDrawable(R.drawable.ic_switch_off_press));
                    }
                    else
                    {
                        ((ImageButton)v).setBackground(getResources().getDrawable(R.drawable.ic_switch_on_press));
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (ib_all_day_flag == false)
                    {
                        ((ImageButton)v).setBackground(getResources().getDrawable(R.drawable.ic_switch_on));
                        ib_all_day_flag = true;
                        if (imgButtonListener_all_day != null) {
                            imgButtonListener_all_day.OnClick(true);
                        }
                    }
                    else
                    {
                        ((ImageButton)v).setBackground(getResources().getDrawable(R.drawable.ic_switch_off));
                        ib_all_day_flag = false;
                        if (imgButtonListener_all_day != null) {
                            imgButtonListener_all_day.OnClick(false);
                        }
                    }
                }
                return false;
            }
        });
        ib_lunar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if (ib_lunar_flag == false)
                    {
                        ((ImageButton)v).setBackground(getResources().getDrawable(R.drawable.ic_switch_off_press));
                    }
                    else
                    {
                        ((ImageButton)v).setBackground(getResources().getDrawable(R.drawable.ic_switch_on_press));
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (ib_lunar_flag == false)
                    {
                        ((ImageButton)v).setBackground(getResources().getDrawable(R.drawable.ic_switch_on));
                        ib_lunar_flag = true;
                        if (imgButtonListener_lunar != null) {
                            imgButtonListener_lunar.OnClick(true);
                        }
                    }
                    else
                    {
                        ((ImageButton)v).setBackground(getResources().getDrawable(R.drawable.ic_switch_off));
                        ib_lunar_flag = false;
                        if (imgButtonListener_lunar != null) {
                            imgButtonListener_lunar.OnClick(false);
                        }
                    }
                }
                return false;
            }
        });

        imgButtonListener_all_day = new ImgButtonListener() {
            @Override
            public void OnClick(boolean flag) {
                if (flag){
                    b_start.setText(calendar_start.get(Calendar.YEAR) + "年" + (calendar_start.get(Calendar.MONTH) + 1) + "月" + calendar_start.get(Calendar.DAY_OF_MONTH) + "日" + " " +
                            "08" + ":" + "00");
                    b_end.setText(calendar_end.get(Calendar.YEAR) + "年" + (calendar_end.get(Calendar.MONTH) + 1) + "月" + (calendar_end.get(Calendar.DAY_OF_MONTH) + 1) + "日" + " " +
                            "08" + ":" + "00");
                    b_start.setEnabled(false);
                    b_end.setEnabled(false);
                }else{
                    b_start.setText(calendar_start.get(Calendar.YEAR) + "年" + (calendar_start.get(Calendar.MONTH) + 1) + "月" + calendar_start.get(Calendar.DAY_OF_MONTH) + "日" + " " +
                            calendar_start.get(Calendar.HOUR_OF_DAY) + ":" + (calendar_start.get(Calendar.MINUTE) == 0 ? "00" : calendar_start.get(Calendar.MINUTE)));
                    if (calendar_end.get(Calendar.HOUR_OF_DAY) <= calendar_start.get(Calendar.HOUR_OF_DAY)) {
                        calendar_end.set(Calendar.HOUR_OF_DAY, calendar_start.get(Calendar.HOUR_OF_DAY) + 1);
                    }
                    b_end.setText(calendar_end.get(Calendar.YEAR) + "年" + (calendar_end.get(Calendar.MONTH) + 1) + "月" + calendar_end.get(Calendar.DAY_OF_MONTH) + "日" + " " +
                            calendar_end.get(Calendar.HOUR_OF_DAY) + ":" + calendar_end.get(Calendar.MINUTE));
                    b_start.setEnabled(true);
                    b_end.setEnabled(true);
                }
            }
        };

        imgButtonListener_lunar = new ImgButtonListener() {
            @Override
            public void OnClick(boolean flag) {
            }
        };

        b_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog time = new TimePickerDialog(AddActivity.this, onTimeSetListener_start, calendar_start.get(Calendar.HOUR_OF_DAY),
                        calendar_start.get(Calendar.MINUTE), true);
                time.show();
            }
        });

        b_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog time = new TimePickerDialog(AddActivity.this, onTimeSetListener_end, calendar_start.get(Calendar.HOUR_OF_DAY) + 1,
                        0, true);
                Resources systemResources = Resources.getSystem();
                int hourNumberPickerId = systemResources.getIdentifier("hour", "id", "android");
                int minuteNumberPickerId = systemResources.getIdentifier("minute", "id", "android");
                NumberPicker hourNumberPicker = time.findViewById(hourNumberPickerId);
                //hourNumberPicker.setMinValue(calendar_start.get(Calendar.HOUR_OF_DAY));
                time.show();
            }
        });

        onTimeSetListener_start = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                calendar_start.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar_start.set(Calendar.MINUTE, minute);
                b_start.setText(calendar_start.get(Calendar.YEAR) + "年" + (calendar_start.get(Calendar.MONTH) + 1) + "月" + calendar_start.get(Calendar.DAY_OF_MONTH) + "日" + " " +
                        calendar_start.get(Calendar.HOUR_OF_DAY) + ":" + (calendar_start.get(Calendar.MINUTE) == 0 ? "00" : calendar_start.get(Calendar.MINUTE)));
                if (calendar_end.get(Calendar.HOUR_OF_DAY) <= calendar_start.get(Calendar.HOUR_OF_DAY)) {
                    calendar_end.set(Calendar.HOUR_OF_DAY, calendar_start.get(Calendar.HOUR_OF_DAY) + 1);
                }
                b_end.setText(calendar_end.get(Calendar.YEAR) + "年" + (calendar_end.get(Calendar.MONTH) + 1) + "月" + calendar_end.get(Calendar.DAY_OF_MONTH) + "日" + " " +
                        calendar_end.get(Calendar.HOUR_OF_DAY) + ":" + calendar_end.get(Calendar.MINUTE));
                Toast.makeText(AddActivity.this, hourOfDay + "hour " + minute + "minute", Toast.LENGTH_SHORT).show();
            }
        };

        onTimeSetListener_end = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                if (hourOfDay <= calendar_start.get(Calendar.HOUR_OF_DAY)) {
                    hourOfDay = calendar_start.get(Calendar.HOUR_OF_DAY) + 1;
                }
                calendar_end.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar_end.set(Calendar.MINUTE, minute);
                b_end.setText(calendar_end.get(Calendar.YEAR) + "年" + (calendar_end.get(Calendar.MONTH) + 1) + "月" + calendar_end.get(Calendar.DAY_OF_MONTH) + "日" + " " +
                        calendar_end.get(Calendar.HOUR_OF_DAY) + ":" + calendar_end.get(Calendar.MINUTE));
                Toast.makeText(AddActivity.this, hourOfDay + "hour " + minute + "minute", Toast.LENGTH_SHORT).show();
            }
        };

        b_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDialog colorDialog = new ColorDialog(AddActivity.this, colorDialogSetInfo);
                colorDialog.show();
            }
        });
        colorDialogSetInfo = new ColorDialog.ColorDialogSetInfo() {
            @Override
            public void onSetInfo(int color, String value) {
                Log.i("ColorActivity return", "color:" + color + "  value:" + value);
                niv_color.setImageDrawable(new ColorDrawable(color));
            }
        };

        b_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setTitle(R.string.add_repeat);
                final String array[] = getResources().getStringArray(R.array.add_array_repeat);
                builder.setSingleChoiceItems(array, checkedItem_repeat, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem_repeat = which;
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (onDismissListener_repeat != null){
                    alertDialog.setOnDismissListener(onDismissListener_repeat);
                }
            }
        });

        b_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setTitle(R.string.add_remind);
                final String array[] = getResources().getStringArray(R.array.add_array_remind);
                boolean checked[] = new boolean[array.length];
                for(int i = 0; i < array.length; i++) {
                    checked[i] = false;
                }
                for(Integer integer : checkedItem_remind) {
                    if (integer < checked.length){
                        checked[integer] = true;
                    }
                }
                builder.setMultiChoiceItems(array, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            if (!checkedItem_remind.contains(which)) {
                                checkedItem_remind.add(which);
                            }
                        }else{
                            if (checkedItem_remind.contains(which)) {
                                checkedItem_remind.remove(which);
                            }
                        }
                    }
                });

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (onDismissListener_remind != null){
                    alertDialog.setOnDismissListener(onDismissListener_remind);
                }
            }
        });

        onDismissListener_repeat = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                final String array[] = getResources().getStringArray(R.array.add_array_repeat);
                b_repeat.setText(array[checkedItem_repeat]);
                Log.i("onDismissListenerRepeat", "你选择了:" + array[checkedItem_repeat]);
            }
        };

        onDismissListener_remind = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                final String array[] = getResources().getStringArray(R.array.add_array_remind);
                String string = new String();
                for (Integer integer : checkedItem_remind) {
                    string += array[integer] + " ";
                }
                if (!string.isEmpty()){
                    b_remind.setText(string);
                }else{
                    b_remind.setText(R.string.no_remind);
                }
                Log.i("onDismissListenerRepeat", "你选择了:" + string);
            }
        };

        if (accountInfoList == null){
            accountInfoList = CalendarApi.ScanAccount(this);
            for(int i = 0; i < accountInfoList.size(); i++){
                if (accountInfoList.get(i).getAccount_name().equals(CalendarApi.CALENDARS_ACCOUNT_NAME)){
                    checkedItem_account = i;
                    break;
                }
            }
        }
        b_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setTitle(R.string.add_account);
                final String array[] = new String[accountInfoList.size()];
                for(int i = 0; i < accountInfoList.size(); i++)
                {
                    array[i] = accountInfoList.get(i).getName() + "\n" + accountInfoList.get(i).getAccount_name();
                }
                builder.setSingleChoiceItems(array, checkedItem_account, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem_account = which;
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (onDismissListener_account != null){
                    alertDialog.setOnDismissListener(onDismissListener_account);
                }
            }
        });
        onDismissListener_account = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                final String array[] = new String[accountInfoList.size()];
                for(int i = 0; i < accountInfoList.size(); i++)
                {
                    array[i] = accountInfoList.get(i).getName() + "\n" + accountInfoList.get(i).getAccount_name();
                }
                b_account.setText(array[checkedItem_account]);
                Log.i("onDismissListenerAcc", "你选择了:" + array[checkedItem_account]);
            }
        };

        button_ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                TextView textView_title = findViewById(R.id.add_title_edit);
                String title = textView_title.getText().toString() != null ? textView_title.getText().toString() : "";
                TextView textView_detail = findViewById(R.id.add_detail_edit);
                String detail = textView_detail.getText().toString() != null ? textView_detail.getText().toString() : "";
                Log.i("button_ok", "title: " + title);
                Log.i("button_ok", "detail: " + detail);
                Log.i("button_ok", "start time: " + calendar_start.get(Calendar.YEAR) + "年" + (calendar_start.get(Calendar.MONTH) + 1) + "月" +
                        calendar_start.get(Calendar.DAY_OF_MONTH) + "日" + " " + calendar_start.get(Calendar.HOUR_OF_DAY) + calendar_start.get(Calendar.MINUTE));
                Log.i("button_ok", "end time: " + calendar_end.get(Calendar.YEAR) + "年" + (calendar_end.get(Calendar.MONTH) + 1) + "月" +
                        calendar_end.get(Calendar.DAY_OF_MONTH) + "日" + " " + calendar_end.get(Calendar.HOUR_OF_DAY) + calendar_end.get(Calendar.MINUTE));
                List<Integer> remind_list = new ArrayList<Integer>();
                if (!checkedItem_remind.isEmpty() && getResources().getStringArray(R.array.add_array_remind).length == getResources().getIntArray(R.array.add_array_remind_int).length) {
                    final int array[] = getResources().getIntArray(R.array.add_array_remind_int);
                    for (Integer integer : checkedItem_remind) {
                        remind_list.add(array[integer]);
                        Log.i("button_ok", "remind: " + array[integer] + "minutes");
                    }
                }

                boolean ret = CalendarApi.addCalendarEvent(AddActivity.this, accountInfoList.get(checkedItem_account).getId(), title, detail,
                        false, calendar_start, calendar_end, "", "", remind_list, getResources().getColor(R.color.color1));
                if (!ret) {
                    Toast.makeText(AddActivity.this, "addCalendarEvent fault", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent();
                //intent.putExtra("data_return", "Hello FirstActivity");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        checkedItem_remind.clear();
        checkedItem_repeat = 0;
        b_color.setText("#" + Integer.toHexString(getResources().getIntArray(R.array.add_color_array)[0]));
        niv_color.setImageDrawable(new ColorDrawable(getResources().getIntArray(R.array.add_color_array)[0]));
        b_repeat.setText(getResources().getStringArray(R.array.add_array_repeat)[0]);
        b_remind.setText(R.string.no_remind);
        calendar_start = java.util.Calendar.getInstance();
        int year = calendar_start.get(Calendar.YEAR);
        int mouth = calendar_start.get(Calendar.MONTH) + 1;
        int day = calendar_start.get(Calendar.DAY_OF_MONTH);
        int hour = calendar_start.get(Calendar.HOUR_OF_DAY);
        calendar_start.set(Calendar.MINUTE, 0);
        calendar_end = (Calendar) calendar_start.clone();
        calendar_end.set(Calendar.HOUR_OF_DAY, hour + 1);
        b_start.setText(year + "年" + mouth + "月" + day + "日" + " " + hour + ":00");  //默认的时间
        b_end.setText(calendar_end.get(Calendar.YEAR) + "年" + (calendar_end.get(Calendar.MONTH) + 1) + "月" + calendar_end.get(Calendar.DAY_OF_MONTH) + "日" + " " + calendar_end.get(Calendar.HOUR_OF_DAY) + ":00");  //默认的时间
        if (accountInfoList != null) {
            b_account.setText(accountInfoList.get(checkedItem_account).getAccount_name());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK)
                {
                    color = data.getIntExtra("color", 0);
                    Log.i("ColorActivity return", "color:" + data.getIntExtra("color", 0) + "  value:" + data.getStringExtra("value"));
                    niv_color.setImageDrawable(new ColorDrawable(color));
                }
                break;
            default:
        }
    }
}
