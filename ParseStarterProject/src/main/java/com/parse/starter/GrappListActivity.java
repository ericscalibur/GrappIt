package com.parse.starter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// this is the GrappIt version

public class GrappListActivity extends AppCompatActivity {

    public static final ArrayList<String> titles = new ArrayList<String>();
    public static final ArrayList<Location> locations = new ArrayList<Location>();

    // move to NewGrapp Activity
    public void getPhoto() {

        Intent intent = new Intent(getApplicationContext(), NewGrappActivity.class);
//        intent.putExtra("username", ParseUser.getCurrentUser().getUsername());
        startActivity(intent);
    }

    // setup options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    // handle options menu selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.newgrapp ) {
            // CREATE NEW GRAPP
            if ( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                getPhoto();
            }
        } else if ( item.getItemId() == R.id.logout ) {
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grapp_list);

        setTitle(ParseUser.getCurrentUser().getUsername()+"'s Grapps");

        final ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> descriptions = new ArrayList<String>();
        final ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        final ArrayList<String> ids = new ArrayList<String>();
        final MyAdapter adapter = new MyAdapter(this, titles, descriptions, images);

        // view clicked item on map
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("placeNumber", i);

            startActivity(intent);
            //Toast.makeText(GrappListActivity.this, "You clicked something!", Toast.LENGTH_SHORT).show();
            }
        });

        // delete Grapp item
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            new AlertDialog.Builder(GrappListActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete this Grapp?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    // delete from Parse
                    String objectId = ids.get(position);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Grapp");
                    query.whereEqualTo("objectId", objectId);
                    query.orderByDescending("createdAt");

                    query.getInBackground(objectId, new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            object.deleteInBackground();
                        } else {
                            // something went wrong
                        }
                        }
                    });

                    //delete from lists
                    titles.remove(position);
                    descriptions.remove(position);
                    images.remove(position);
                    ids.remove(position);
                    locations.remove(position);
                    adapter.notifyDataSetChanged();
                    }

                })
                .setNegativeButton("No", null)
                .show();

            return true;
            }
        });

        //query parse for images to fill ListView
        ParseQuery<ParseObject> grappQuery = new ParseQuery<ParseObject>("Grapp");
        grappQuery.whereEqualTo("username",  ParseUser.getCurrentUser().getUsername());
        grappQuery.orderByDescending("createdAt");

        grappQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
            if (e == null && objects.size() > 0) {
                for (ParseObject object : objects ) {

                    String newTitle = (String) object.getString("title");
                    String newDescription = (String) object.getString("description");
                    ParseGeoPoint geoPoint = (ParseGeoPoint) object.getParseGeoPoint("geopoint");
                    Location location = new Location("");

                    if( geoPoint != null ) {
                        double latitude = geoPoint.getLatitude();
                        double longitude = geoPoint.getLongitude();


                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                    }

                    titles.add(newTitle);
                    descriptions.add(newDescription);
                    ids.add(object.getObjectId());
                    locations.add(location);

                    ParseFile file = (ParseFile) object.get("image");

                    //download images from Parse ImageObject file
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null && data != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                images.add(bitmap);
                                listView.setAdapter(adapter);

                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> myTitles;
        ArrayList<Bitmap> bitmapArrayList;
        ArrayList<String> myDescriptions;

        MyAdapter(Context c, ArrayList<String> titles, ArrayList<String> desc, ArrayList<Bitmap> imgs) {
            super(c, R.layout.activity_grapp_list, R.id.itemTitleTextView, titles);
            this.context = c;
            this.bitmapArrayList = imgs;
            this.myTitles = titles;
            this.myDescriptions = desc;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listItem = layoutInflater.inflate(R.layout.list_item_layout, parent, false);
            ImageView thumbImage =  (ImageView) listItem.findViewById(R.id.thumbImageView);
            TextView itemTitle = (TextView) listItem.findViewById(R.id.itemTitleTextView);
            TextView itemDescription = (TextView) listItem.findViewById(R.id.descriptionTextView);

            if(bitmapArrayList.size() > position) {
                if (bitmapArrayList.get(position) != null) {
                    thumbImage.setImageBitmap(bitmapArrayList.get(position));
                } else {
                    Toast.makeText(GrappListActivity.this, "bitmapArrayList == null", Toast.LENGTH_SHORT).show();
                }

                if (myTitles.get(position) != null) {
                    itemTitle.setText(myTitles.get(position));
                } else {
                    Toast.makeText(GrappListActivity.this, "myTitle: " + myTitles.get(position), Toast.LENGTH_SHORT).show();
                }

                if (myDescriptions.get(position) != null) {
                    itemDescription.setText(myDescriptions.get(position));
                } else {
                    // handle
                }
            }

            return listItem;
        }
    }
}