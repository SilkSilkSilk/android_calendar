package com.example.mycalendar.Event;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycalendar.R;

import java.util.List;

class EvnetInfoViewHolder extends RecyclerView.ViewHolder{
    View view;
    TextView id;
    ImageView color;
    TextView text;

    public EvnetInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        color = itemView.findViewById(R.id.event_list_item_image);
        id = itemView.findViewById(R.id.event_list_item_text1);
        text = itemView.findViewById(R.id.event_list_item_text2);
    }
}

public class EventAdapter extends RecyclerView.Adapter<EvnetInfoViewHolder> {
    private List<EventInfo> mEventInfoList;

    public EventAdapter(List<EventInfo> mEventInfoList) {
        this.mEventInfoList = mEventInfoList;
    }

    @NonNull
    @Override
    public EvnetInfoViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        final EvnetInfoViewHolder viewHolder = new EvnetInfoViewHolder(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventInfoList == null || viewHolder.getAdapterPosition() > mEventInfoList.size()){
                    return;
                }
                EventInfo eventinfo = mEventInfoList.get(viewHolder.getAdapterPosition());
                Toast.makeText(v.getContext(), "clicked " + eventinfo.getId() + ":" + eventinfo.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull EvnetInfoViewHolder holder, int position) {
        if (mEventInfoList == null || position > mEventInfoList.size()){
            return;
        }
        EventInfo eventinfo = mEventInfoList.get(position);
        holder.id.setText(String.valueOf(eventinfo.getId()));
        holder.color.setBackgroundColor(eventinfo.getColor());   //后面再选择array
        holder.text.setText(eventinfo.getText());
    }

    @Override
    public int getItemCount() {
        if (mEventInfoList == null){
            return 0;
        }
        return mEventInfoList.size();
    }
}
