package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.parse.starter.NewGrappActivity.rotate;

public class ViewGrappActivity extends AppCompatActivity {

    Integer i = 0;
    ArrayList<Grapp> grappList = GrappListActivity.grappList;

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grapp);

        Intent intent = getIntent();
        i = intent.getIntExtra("placeNumber", 0);

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView descTextView = (TextView) findViewById(R.id.descTextView);
        TextView viewOnMapButton = (TextView) findViewById(R.id.viewMapButton);
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView);

        Grapp currentGrapp = grappList.get(i);
        Bitmap bitmap = currentGrapp.getBitmap();

        if( bitmap.getWidth() > bitmap.getHeight() ) {
            imageView.setImageBitmap(rotate(bitmap, 90));
        } else {
            imageView.setImageBitmap(bitmap);
        }

        titleTextView.setText(currentGrapp.getTitle());
        descTextView.setText(currentGrapp.getDesc());
        dateTextView.setText(currentGrapp.getBirthday());
    }

    public void viewOnMap(View view) {
        if(view.getId() == R.id.viewMapButton) {

            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("placeNumber", i);
            startActivity(intent);
        }
    }
}
