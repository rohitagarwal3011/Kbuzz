package com.app.rohit.campk12_videochat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.opentok.exception.OpenTokException;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by rohit on 10/1/18.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{

    private List<User> data;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView email;
        Button invite_button;
        Button user_icon;

        Button reject_button;
        public MyViewHolder(View view)
        {
            super(view);

            name = (TextView)view.findViewById(R.id.name);
            invite_button = (Button)view.findViewById(R.id.invite_button);
            user_icon = (Button)view.findViewById(R.id.user_icon);
            email = (TextView)view.findViewById(R.id.email);
            reject_button = (Button)view.findViewById(R.id.reject_button);

            invite_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(invite_button.getText().toString().equalsIgnoreCase("Connect")) {
                        if(((HomeActivity)context).current_user.invite_sent_to!= null)
                        {
                            if(((HomeActivity)context).current_user.invite_sent_to.length()==0)
                            {
                                try {
                                    ((HomeActivity) context).on_invite_clicked(data.get(getAdapterPosition()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {

                            }
                        }

                    }
                    else if (invite_button.getText().toString().equalsIgnoreCase("Accept")) {
                        try {
                            ((HomeActivity) context).generate_Session_id(data.get(getAdapterPosition()));
                        } catch (OpenTokException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(invite_button.getText().toString().equalsIgnoreCase("Cancel"))
                    {

                        ((HomeActivity)context).on_invite_cancel(data.get(getAdapterPosition()));
                    }

                }
            });

            reject_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((HomeActivity)context).on_reject_invite(data.get(getAdapterPosition()));
                }
            });
        }


    }

    public UserAdapter(Context context, List<User> data)
    {
        this.context=context;
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

       holder.name.setText(data.get(position).name);
        holder.user_icon.setText(data.get(position).name.substring(0,1));
        holder.email.setText(data.get(position).email);


        User current_user = ((HomeActivity)context).current_user;
        if(current_user.invite_recieved_from!=null)
        {
            if(current_user.invite_recieved_from.equalsIgnoreCase(data.get(position).email))
            {
                holder.invite_button.setText("Accept");
                holder.invite_button.setBackground(context.getDrawable(R.drawable.connect_green));

                holder.reject_button.setVisibility(View.VISIBLE);

            }
        }

        if(current_user.invite_sent_to!=null)
        {
            if(current_user.invite_sent_to.equalsIgnoreCase(data.get(position).email))
            {
                holder.invite_button.setText("Cancel");
                holder.invite_button.setBackground(context.getDrawable(R.drawable.connect_red));
                holder.reject_button.setVisibility(View.GONE);
            }

        }

        if(current_user.connected!=null) {
            if (current_user.connected) {
                holder.reject_button.setVisibility(View.GONE);
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        }

        if(data.get(position).connected!=null)
        {
            if(data.get(position).connected )
            {
                holder.invite_button.setText("Busy");
                holder.invite_button.setBackground(context.getDrawable(R.drawable.connect_grey));
                holder.reject_button.setVisibility(View.GONE);
            }
        }

        if(!(data.get(position).email.equalsIgnoreCase(current_user.invite_recieved_from)||data.get(position).email.equalsIgnoreCase(current_user.invite_sent_to)))
        {

            if(data.get(position).invite_recieved_from.length()>0 || data.get(position).invite_sent_to.length()>0)
            {
                holder.invite_button.setText("Busy");
                holder.invite_button.setBackground(context.getDrawable(R.drawable.connect_grey));
                holder.reject_button.setVisibility(View.GONE);
            }
        }



    }





    @Override
    public int getItemCount() {
        return data.size();
    }
}
