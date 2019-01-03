package com.parse.starter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    Context myContext;
    LayoutInflater inflater;
    List<Grapp> modelList;
    ArrayList<Grapp> grappList;
    ArrayList<Bitmap> iconList;
    ArrayList<Bitmap> imageList;

    public ListViewAdapter(Context context, List<Grapp> modelList) {
        this.myContext = context;
        this.modelList = modelList;

        inflater = LayoutInflater.from(myContext);
        this.grappList = new ArrayList<Grapp>();
        this.grappList.addAll(modelList);
    }

    public class ViewHolder {
        TextView titleTextView, descTextView;
        ImageView iconImageView;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int i) {
        return modelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item_layout, null);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.titleTextView = (TextView) view.findViewById(R.id.itemTitleTextView);
        holder.descTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        holder.iconImageView = (ImageView) view.findViewById(R.id.thumbImageView);

        if(modelList.size() > position ) {

            holder.iconImageView.setImageBitmap(modelList.get(position).getBitmap());
            holder.titleTextView.setText(modelList.get(position).getTitle());
            holder.descTextView.setText(modelList.get(position).getDesc());
        }

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        modelList.clear();
        if(charText.length() == 0) {
            modelList.addAll(grappList);
        } else {
            for (int i = 0; i < modelList.size(); i++ ) {
                if (grappList.get(i).getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    modelList.add(grappList.get(i));
                }
            }
        }

        notifyDataSetChanged();
    }
}