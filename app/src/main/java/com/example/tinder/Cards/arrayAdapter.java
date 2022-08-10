package com.example.tinder.Cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tinder.Model;
import com.example.tinder.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<Model> {
    Context context;

    public arrayAdapter(Context context, int resourceId, List<Model> items) {
        super(context, resourceId, items);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        //cards cards_item = getItem(position);
        Model model = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(model.getName());
        switch (model.getImageUrl()) {
            case "default":
                Glide.with(getContext()).load(R.mipmap.ic_launcher).into(image);
                break;
            default:
                Glide.with(image).clear(image);
                Glide.with(getContext()).load(model.getImageUrl()).into(image);
                break;
        }


        return convertView;
    }
}
