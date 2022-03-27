package com.example.pracainfrag.adverts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pracainfrag.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.MyViewHolder> {

    List<AdvertModel> advertList;
    Context context;
    FragmentManager fragmentManager;
    private boolean editable = false;

    public AdvertAdapter(List<AdvertModel> advertList, Context context, FragmentManager fragmentManager) {
        this.advertList = advertList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_advert,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        AdvertModel advertModel = advertList.get(holder.getAdapterPosition());
        Date date = advertModel.getDepartureTime().toDate();
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
        String datetime = dateformat.format(date.getTime());
        dateformat = new SimpleDateFormat("dd-MM-yyyy");
        datetime += "  " + dateformat.format(date.getTime());

        if(date.getTime() < Calendar.getInstance().getTimeInMillis()){
            holder.parentLayout.setBackgroundResource(R.color.notActiveAdvert);
        }
        else
            holder.parentLayout.setBackgroundResource(R.color.white);

        holder.leavingCity.append(advertModel.getDepartureCity());
        holder.destination.append(advertModel.getDestinationCity());
        holder.date.append(datetime);
        holder.price.append(String.valueOf(advertModel.getPrice()));


        holder.parentLayout.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("advertModel", advertModel);
            AdvertFragment advertFragment = new AdvertFragment();
            advertFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, advertFragment).addToBackStack(null).commit();
        });



    }

    @Override
    public int getItemCount() {
        return advertList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date, leavingCity, destination,price;

        ConstraintLayout parentLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.adapterDateTV);
            leavingCity = itemView.findViewById(R.id.adapterLeavingCity);
            destination = itemView.findViewById(R.id.adapterDestinationTV);
            price = itemView.findViewById(R.id.adapterPrice);
            parentLayout = itemView.findViewById(R.id.advertLayout);
        }
    }
}
