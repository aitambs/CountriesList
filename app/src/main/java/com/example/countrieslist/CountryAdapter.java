package com.example.countrieslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> {

    public List<Country> dataSet = new ArrayList<>();

    CountryAdapter(List<Country> dataSet) {
        this.dataSet.addAll(dataSet);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(dataSet.get(position).name);
        holder.nativeName.setText(dataSet.get(position).nativeName);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, nativeName;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.country_name);
            nativeName=itemView.findViewById(R.id.country_native_name);
        }
    }
}
