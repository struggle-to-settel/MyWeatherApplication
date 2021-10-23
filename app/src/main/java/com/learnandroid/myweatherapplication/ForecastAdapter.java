package com.learnandroid.myweatherapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private static final String TAG = "ForecastAdapter";
    public static final String MIN = "Min\n";
    public static final String MAX = "Max\n";
    public static final String CENT = "\u00B0";
    Context context;
    List<WeatherData> list = new ArrayList<>();

    public ForecastAdapter(Context context){
        this.context = context;
    }

    public void setList(List<WeatherData> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_card,parent,false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherData data = list.get(position);
        holder.date.setText(data.getDate());
        Log.d(TAG, "onBindViewHolder: "+data.getMin_temp());
        holder.condition.setText(data.getCondition());
        holder.max.setText(data.getMx_temp()+CENT);
        holder.min.setText(data.getMin_temp()+CENT);
        int id = context.getResources().getIdentifier(data.getIcon(),"drawable",context.getPackageName());
        holder.imageView.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView date,min,max,condition;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           date = itemView.findViewById(R.id.card_date);
           min = itemView.findViewById(R.id.card_centigrade_min);
           max = itemView.findViewById(R.id.card_centigrade_max);
           condition = itemView.findViewById(R.id.condition);
           imageView = itemView.findViewById(R.id.card_image);
        }
    }
}
