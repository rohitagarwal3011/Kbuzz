package com.app.rohit.campk12_drawnshare;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rohit on 8/1/18.
 */

public class Image_adapter extends RecyclerView.Adapter<Image_adapter.MyViewHolder>{

    private List<String> data;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView image_name;
        public MyViewHolder(View view)
        {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            image_name  = (TextView)view.findViewById(R.id.image_name);
        }

    }

    public Image_adapter(Context context, List<String> data)
    {
        this.context=context;
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Picasso.with(context).load("file://"+data.get(position)).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
