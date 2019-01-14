package com.parse.starter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter implements Filterable {

    Context myContext;
    GrappFilter grappFilter;
    ArrayList<Grapp> grappList;
    ArrayList<Grapp> filteredList;


    public ListViewAdapter(Context context, ArrayList<Grapp> modelList) {
        this.myContext = context;
        this.grappList = modelList;
        this.filteredList = modelList;

        getFilter();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        Grapp thisGrapp = filteredList.get(position);
        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_layout, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) view.findViewById(R.id.itemTitleTextView);
            holder.descTextView = (TextView) view.findViewById(R.id.descriptionTextView);
            holder.iconImageView = (ImageView) view.findViewById(R.id.thumbImageView);
            holder.dateTextView = (TextView) view.findViewById(R.id.dateTextView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if(thisGrapp.getOrientation() == "portrait")  {
            holder.iconImageView.setImageBitmap(rotate(thisGrapp.getBitmap(), 90));
        } else {
            holder.iconImageView.setImageBitmap(thisGrapp.getBitmap());
        }

        holder.titleTextView.setText(thisGrapp.getTitle());
        holder.descTextView.setText(thisGrapp.getDesc());
        holder.dateTextView.setText(thisGrapp.getBirthday());


//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
        return view;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public Filter getFilter() {
        if (grappFilter == null) {
            grappFilter = new GrappFilter();
        }
        return grappFilter;
    }

    public class ViewHolder {

        TextView titleTextView, descTextView, dateTextView;
        ImageView iconImageView;
    }


//    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        grappList.clear();
//        if(charText.length() == 0) {
//            grappList.addAll(filteredList);
//        } else {
//            for (int i = 0; i < grappList.size(); i++ ) {
//                if (grappList.get(i).getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    grappList.add(filteredList.get(i));
//                }
//            }
//        }
//
//        notifyDataSetChanged();
//    }

    private class GrappFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Grapp> tempList = new ArrayList<Grapp>();

                // search content in grapp list
                for (Grapp grapp : grappList) {
                    if (grapp.getTitle().toLowerCase(Locale.getDefault()).contains(constraint)) {
                        tempList.add(grapp);
                        Log.i("Check", grapp.getTitle().toLowerCase(Locale.getDefault()) +" contains "+constraint);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = grappList.size();
                filterResults.values = grappList;
            }

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Grapp>) results.values;
            notifyDataSetChanged();
        }
    }
}