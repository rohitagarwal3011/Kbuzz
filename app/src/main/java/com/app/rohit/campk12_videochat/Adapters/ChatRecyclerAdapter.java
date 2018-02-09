package com.app.rohit.campk12_videochat.Adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rohit.campk12_videochat.Models.Chat;
import com.app.rohit.campk12_videochat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 10/16/2016 , 10:36 AM
 * Project: FirebaseChat
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    private static final int VIEW_IMAGE_ME=3;
    private static final int VIEW_IMAGE_OTHER=4;

    Context context;

    private List<Chat> mChats;

    public ChatRecyclerAdapter(List<Chat> chats,Context context) {
        this.mChats = chats;
        this.context=context;
    }

    public void add(Chat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
            case VIEW_IMAGE_ME:
                View viewChatImageMe = layoutInflater.inflate(R.layout.item_chat_mine_image, parent, false);
                viewHolder = new MyChatImageViewHolder(viewChatImageMe);
                break;
            case VIEW_IMAGE_OTHER:
                View viewChatImageOther = layoutInflater.inflate(R.layout.item_chat_other_image, parent, false);
                viewHolder = new OtherChatImageViewHolder(viewChatImageOther);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            if(mChats.get(position).type.equalsIgnoreCase("Text"))
                configureMyChatViewHolder((MyChatViewHolder) holder, position);
            else
                configureMyImageChatViewHolder((MyChatImageViewHolder) holder, position);
        } else {
            if(mChats.get(position).type.equalsIgnoreCase("Text"))
                configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
            else
                configureOtherImageChatViewHolder((OtherChatImageViewHolder)holder,position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);

        myChatViewHolder.txtChatMessage.setText(chat.message);
        myChatViewHolder.txtUserAlphabet.setText(alphabet);
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);

        otherChatViewHolder.txtChatMessage.setText(chat.message);
        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
    }

    private void configureMyImageChatViewHolder(MyChatImageViewHolder myChatImageViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);

        Picasso.with(context).load(chat.message).into(myChatImageViewHolder.imageChat);
        myChatImageViewHolder.txtUserAlphabet.setText(alphabet);
    }

    private void configureOtherImageChatViewHolder(OtherChatImageViewHolder otherChatImageViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);

        Picasso.with(context).load(chat.message).into(otherChatImageViewHolder.imageChat);
        otherChatImageViewHolder.txtUserAlphabet.setText(alphabet);
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            if(mChats.get(position).type.equalsIgnoreCase("Text"))
               return VIEW_TYPE_ME;
            else
                return VIEW_IMAGE_ME;
        } else {
            if(mChats.get(position).type.equalsIgnoreCase("Text"))
                return VIEW_TYPE_OTHER;
            else
               return VIEW_IMAGE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }

    private static class MyChatImageViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUserAlphabet;

        private ImageView imageChat;
        public MyChatImageViewHolder(View itemView) {
            super(itemView);
            imageChat = (ImageView) itemView.findViewById(R.id.chat_image);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }

    private static class OtherChatImageViewHolder extends RecyclerView.ViewHolder {
        private TextView  txtUserAlphabet;

        private ImageView imageChat;
        public OtherChatImageViewHolder(View itemView) {
            super(itemView);
            imageChat = (ImageView) itemView.findViewById(R.id.chat_image);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }
}
