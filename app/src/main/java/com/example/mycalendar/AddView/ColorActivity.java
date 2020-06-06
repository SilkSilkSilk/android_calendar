package com.example.mycalendar.AddView;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycalendar.R;
import com.example.mycalendar.base.activity.BaseActivity;
import com.shehuan.niv.NiceImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ColorInfo
{
    private int color;
    private String value;

    ColorInfo(int color, String value) {
        this.color = color;
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class ColorViewHolder extends RecyclerView.ViewHolder{
    View view;
    NiceImageView image;
    TextView value;

    public ColorViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        image = itemView.findViewById(R.id.color_list_item_image);
        value = itemView.findViewById(R.id.color_list_item_text);
    }
}

interface ColorItemClickListener{
     void onClick(ColorViewHolder viewHolder, View v, int pos);
}

class ColorAdapter extends RecyclerView.Adapter<ColorViewHolder> {
    private List<ColorInfo> mColorList;
    ColorItemClickListener colorItemClickListener;

    ColorAdapter(List<ColorInfo> mColorList, ColorItemClickListener colorItemClickListener) {
        this.mColorList = mColorList;
        this.colorItemClickListener = colorItemClickListener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_list_item, parent, false);
        final ColorViewHolder viewHolder = new ColorViewHolder(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorInfo colorInfo = mColorList.get(viewHolder.getAdapterPosition());
                Toast.makeText(v.getContext(), "clicked " + colorInfo.getValue(), Toast.LENGTH_SHORT).show();
                if (colorItemClickListener != null)
                {
                    colorItemClickListener.onClick(viewHolder, view, viewHolder.getAdapterPosition());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        ColorInfo colorInfo = mColorList.get(position);
//        holder.image.setBackgroundColor(0xffffff);
        holder.image.setImageDrawable(new ColorDrawable(colorInfo.getColor()));
        holder.value.setText(colorInfo.getValue());
    }

    @Override
    public int getItemCount() {
        return mColorList.size();
    }
}

class ColorDialog extends AlertDialog {
    RecyclerView recyclerView;
    ColorAdapter colorAdapter;
    List<ColorInfo> list = new ArrayList<>();
    ColorItemClickListener colorItemClickListener;
    ColorDialogSetInfo colorDialogSetInfo;

    public interface ColorDialogSetInfo{
        void onSetInfo(int color, String value);
    }

    public ColorDialog(Context context, ColorDialogSetInfo setInfo) {
        super(context);
        colorDialogSetInfo = setInfo;
        initView();
    }

    void initView() {
        final Context Context = getContext();
        final LayoutInflater inflater = LayoutInflater.from(Context);
        final View view = inflater.inflate(R.layout.activity_color, null);
        setView(view);
        this.setTitle(R.string.color_select);
        final int color[] = Context.getResources().getIntArray(R.array.add_color_array);
        recyclerView = view.findViewById(R.id.color_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Context);
        recyclerView.setLayoutManager(linearLayoutManager);
        list_data_init(list);
        colorItemClickListener = new ColorItemClickListener() {
            @Override
            public void onClick(ColorViewHolder viewHolder, View v, int pos) {
                colorDialogSetInfo.onSetInfo(list.get(pos).getColor(), list.get(pos).getValue());
                dismiss();
            }
        };
        colorAdapter = new ColorAdapter(list, colorItemClickListener);
        recyclerView.setAdapter(colorAdapter);
    }

    private void list_data_init(List<ColorInfo> list) {
        list.clear();
        final int color[] = getContext().getResources().getIntArray(R.array.add_color_array);
        Log.i("color", "color[0]:" + color[0]);
        for (int i = 0; i < color.length; i++)
        {
            ColorInfo colorInfo = new ColorInfo(color[i], "#" + Integer.toHexString(color[i]));
            list.add(colorInfo);
        }
    }
}
