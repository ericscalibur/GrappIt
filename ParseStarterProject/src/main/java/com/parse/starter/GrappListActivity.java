package com.parse.starter;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;


import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

// this is the GrappIt version

public class GrappListActivity extends AppCompatActivity {

    static ArrayList<Grapp> grappList = new ArrayList<Grapp>();

    ListView listView;
    static ListViewAdapter adapter;

    Bitmap bitmap;

    // setup options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

//        return super.onCreateOptionsMenu(menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if(searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    if (TextUtils.isEmpty(s)) {
                        adapter.getFilter().filter("");
                        listView.clearTextFilter();
                    } else {
                        adapter.getFilter().filter(s);
                    }
                    return true;
                }
            });
        }

        return true;
    }


    // handle options menu selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.newgrapp ) {
            // CREATE NEW GRAPP
            if ( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                Intent intent = new Intent(getApplicationContext(), NewGrappActivity.class);
                startActivity(intent);
            }
        } else if ( item.getItemId() == R.id.logout ) {
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if( item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(GrappListActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("This will log you out.")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    ParseUser.logOut();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    }

                })
                .setNegativeButton("No", null)
                // .setNeutralButton("Retake Photo")
                .show();
    }

    public class CustomComparator implements Comparator<Grapp> {// MyObject would be Model class
        Date date1,date2;
        @Override
        public int compare(Grapp obj1, Grapp obj2) {
            DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
            try {
                date1 = df1.parse(obj1.getBirthday());
                date2 = df1.parse(obj2.getBirthday());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return date1.compareTo(date2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grapp_list);

        setTitle(ParseUser.getCurrentUser().getUsername()+"'s Grapps");

        grappList.clear();

        listView = (ListView) findViewById(R.id.listView);

        adapter = new ListViewAdapter(GrappListActivity.this, grappList );
        listView.setAdapter(adapter);

        //query parse for data to fill ListView
        ParseQuery<ParseObject> grappQuery = new ParseQuery<ParseObject>("Grapp");
        grappQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        grappQuery.orderByDescending("birthday");

        grappQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
            if (e == null && objects.size() > 0) {
                for (final ParseObject object : objects ) {

                    ParseFile file = (ParseFile) object.get("image");

                    //download images from Parse ImageObject file
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                        if (e == null && data != null) {

                            String newTitle = (String) object.getString("title");
                            String newDescription = (String) object.getString("description");
                            ParseGeoPoint geoPoint = (ParseGeoPoint) object.getParseGeoPoint("geopoint");
                            Location location = new Location("");
                            String id = (String) object.getObjectId();
                            String date = object.getString("birthday");
                            String orientation = object.getString("orientation");

                            if( geoPoint != null ) {
                                double latitude = geoPoint.getLatitude();
                                double longitude = geoPoint.getLongitude();

                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                            }

                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                            Grapp newGrapp = new Grapp(newTitle, newDescription, bitmap, id, location, date, orientation);
                            grappList.add(newGrapp);
                            Collections.sort(grappList, new CustomComparator());
                            Collections.reverse(grappList);
                            adapter.notifyDataSetChanged();

                        } else {
                            e.printStackTrace();
                        }
                        }
                    });
                }
            } else {
                    e.printStackTrace();
                }
            }
        });

        // view clicked item on map
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getApplicationContext(), ViewGrappActivity.class);
            intent.putExtra("placeNumber", i);
            startActivity(intent);
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
                    String objectId = grappList.get(position).getId();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Grapp");
                    query.whereEqualTo("objectId", objectId);
                    query.orderByDescending("birthday");

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
                    grappList.remove(position);
                    adapter.notifyDataSetChanged();
                    }

                })
                .setNegativeButton("No", null)
                .show();

            return true;
            }
        });
    }
}
