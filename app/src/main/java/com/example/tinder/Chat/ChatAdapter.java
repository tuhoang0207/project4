package com.example.tinder.Chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinder.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private List<ChatObject> chatList;
    private Context context;

    public ChatAdapter(List<ChatObject> matchesList, Context context) {
        this.chatList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ChatViewHolder rcv = new ChatViewHolder((layoutView));

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());
        if (chatList.get(position).getCurrentUser()) { // nguoi gui
            holder.mMessage.setGravity(Gravity.END);
            holder.mMessage.setTextSize(24);
            holder.mMessage.setTextColor(Color.parseColor("#404040"));
            holder.mMessage.setPadding(15,15,15,15);
            holder.mContainer.setBackgroundColor(Color.parseColor("#d993d0"));
            holder.mContainer.setPadding(20,20,20,20);
        } else {
            holder.mMessage.setGravity(Gravity.START);
            holder.mMessage.setTextSize(24);
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mMessage.setPadding(15,15,15,15);
            holder.mContainer.setBackgroundColor(Color.parseColor("#2DB4CB"));
            holder.mContainer.setPadding(20,20,20,20);
        }
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
