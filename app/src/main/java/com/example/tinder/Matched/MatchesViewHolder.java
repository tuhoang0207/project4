package com.example.tinder.Matched;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinder.Chat.ChatActivity;
import com.example.tinder.MainActivity;
import com.example.tinder.R;

public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId,mMatchName,mMatchSex;
    public ImageView mMatchImage;
    public MatchesViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
//        mMatchSex = (TextView) itemView.findViewById(R.id.MatchSex1);

        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId",mMatchId.getText().toString());
//        b.putString("matchSex",mMatchSex.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
