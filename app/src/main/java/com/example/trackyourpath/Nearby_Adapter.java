package com.example.trackyourpath;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ImageView;
import android.widget.TextView;

//import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Nearby_Adapter extends RecyclerView.Adapter<Nearby_Adapter.NearbyViewHolder>{
    private Context mcontext;
    private ArrayList<Nearby_Value> N_values;

    public Nearby_Adapter(Context context,ArrayList<Nearby_Value> values)
    {
        mcontext=context;
        N_values=values;
    }
    @NonNull
    @Override
    public NearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mcontext).inflate(R.layout.nearby_value,parent,false);
        return new NearbyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyViewHolder nearbyViewHolder, int position) {

        Nearby_Value current_value=N_values.get(position);
        String name=current_value.getName();
        String rating=current_value.getRating();
       // Picasso.get().load(current_value.getIcon()).into(nearbyViewHolder.image);
        nearbyViewHolder.name.setText("Name:-"+name);
        nearbyViewHolder.rating.setText("Rating:-"+rating);
    }

    @Override
    public int getItemCount() {
        return N_values.size();
    }

    public class NearbyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView rating;
        //public ImageView image;

        public NearbyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            rating=itemView.findViewById(R.id.rating);
           // image=itemView.findViewById(R.id.R_image);
        }
    }
}
