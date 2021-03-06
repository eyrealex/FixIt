package com.alexeyre.fixit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alexeyre.fixit.Activities.InspectionViewActivity;
import com.alexeyre.fixit.Models.TrafficLightModel;
import com.alexeyre.fixit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListViewHolder> {

    //variables
    private Context context;
    private ArrayList<TrafficLightModel> trafficLightModels = new ArrayList<>();
    private int lastPosition = -1;
    private Calendar calendar = Calendar.getInstance();
    private DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String getMyCurrentDateTime;

    public ReportListAdapter(Context context, ArrayList<TrafficLightModel> trafficLightModels) {
        this.context = context;
        this.trafficLightModels = trafficLightModels;
    }


    @NonNull
    @Override
    public ReportListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //create the adapter and set the adapter with variables, create on onclick for the adapter
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inspections_list_item, viewGroup, false);
        return new ReportListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportListViewHolder reportListViewHolder, int position) {

        //set the template lists with data using ID and Location from TrafficLightModel
        reportListViewHolder.id_tv.setText(String.format(Locale.ENGLISH, "ID: %s", trafficLightModels.get(position).getkey()).trim());
        reportListViewHolder.location_tv.setText(String.format(Locale.ENGLISH, "Location: %s", trafficLightModels.get(position).getname()).trim());

        //set the template lists with data when the inspection was reported from TrafficLightInspectionModel
        //if there has been no inspections previously reported, print out N/A

        if (trafficLightModels.get(position) == null) {
            reportListViewHolder.reported_on_tv.setText(String.format(Locale.ENGLISH, "Reported on: N/A"));
        } else {

            String newDate = trafficLightModels.get(position).gettimestamp().trim();
            calendar.setTimeInMillis(Long.parseLong(newDate));
            getMyCurrentDateTime = formatter.format(calendar.getTime());
           reportListViewHolder.reported_on_tv.setText(String.format(Locale.ENGLISH, "Reported on: %s", getMyCurrentDateTime));
        }

        reportListViewHolder.created_by.setText(String.format(Locale.ENGLISH, "Created by: %s", trafficLightModels.get(position).getinspection_by()).trim());

        //create onclick for each list item
        reportListViewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle ();
                bundle.putString("inspection_key", trafficLightModels.get(position).getkey());
                bundle.putString("inspection_timestamp", trafficLightModels.get(position).gettimestamp());
                bundle.putString("inspection_path", trafficLightModels.get(position).getpath());
                Intent inspectionIntent = new Intent(context, InspectionViewActivity.class);
                inspectionIntent.putExtra("bundle", bundle);
                context.startActivity(inspectionIntent);
            }
        });

    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {

            ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return trafficLightModels.size();
    }
}

class ReportListViewHolder extends RecyclerView.ViewHolder {
    TextView id_tv, location_tv, reported_on_tv, created_by;
    CardView parent;


    public ReportListViewHolder(@NonNull View itemView) {
        super(itemView);

        id_tv = itemView.findViewById(R.id.id_tv);
        location_tv = itemView.findViewById(R.id.location_tv);
        reported_on_tv = itemView.findViewById(R.id.reported_on_tv);
        created_by = itemView.findViewById(R.id.created_by_tv);
        parent = itemView.findViewById(R.id.parent_cv);
    }


}
