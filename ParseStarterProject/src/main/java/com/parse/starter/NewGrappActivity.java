package com.parse.starter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// this is the GrappIt version

public class NewGrappActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    ImageView imageView;
    EditText titleEditText;
    EditText descriptionEditText;

    Bitmap bitmap;

    private static final int REQUEST_LOCATION = 1;
    static LocationManager locationManager;

    LocationListener locationListener;

    Location lastKnownLocation;

    public void saveFunction(View view) {

        // get user location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                   centerMapOnLocation(location, "Your location");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0,0, locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // make geoPoint
        ParseGeoPoint point = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        // save Grapp to Parse
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        ParseFile file = new ParseFile("image.png", byteArray);

        ParseObject object = new ParseObject("Grapp");

        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        final String reportDate = df.format(currentTime);

        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("image", file);
        object.put("title", titleEditText.getText().toString());
        object.put("description", descriptionEditText.getText().toString());
        object.put("geopoint", point);
        object.put("birthday", reportDate);

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            if (e == null) {
                Toast.makeText(NewGrappActivity.this, "Successfully Grapped at "+reportDate, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), GrappListActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText( NewGrappActivity.this, "there has been an error", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    public void cancelFunction(View view) {

        new AlertDialog.Builder(NewGrappActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure?")
                .setMessage("Do you want to cancel?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // clear fields
                        titleEditText.setText("");
                        descriptionEditText.setText("");
                    }

                })
                .setNegativeButton("No", null)
                // .setNeutralButton("Retake Photo")
                .show();

    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if ( i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            saveFunction(view);
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(NewGrappActivity.this, GrappListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grapp);

        setTitle("New Grapp");

        // launch camera first thing to get image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);

        imageView = (ImageView) findViewById(R.id.imageView);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        ConstraintLayout background = (ConstraintLayout) findViewById(R.id.background);

        // incase user taps off keyboard...
        imageView.setOnClickListener(this);
        background.setOnClickListener(this);

        // allow 'KEYCODE_ENTER' press to call saveFunction()...
        descriptionEditText.setOnKeyListener(this);

    }

    // after user takes photo, convert to bitmap, then to byteArray
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        bitmap = (Bitmap) data.getExtras().get("data");
        Log.i("image selected", "good work");

        imageView.setImageBitmap(bitmap);
    }

    // if permission granted, get location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0,0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
    }

    // close keyboard when user taps on background or image
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.background || view.getId() == R.id.imageView) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
