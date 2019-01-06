package com.parse.starter;

import android.graphics.Bitmap;
import android.location.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Grapp {

    String title;
    String desc;
    String id;
    Bitmap img;
    Location location;
    String birthday;

    public Grapp(String title, String desc, Bitmap img, String id, Location location, String date) {
        this.title = title;
        this.desc = desc;
        this.id = id;
        this.img = img;
        this.location = location;
        this.birthday = date;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getId() {
        return this.id;
    }

    public Location getLocation() {
        return this.location;
    }

    public Bitmap getBitmap() { return this.img; }

    public String getBirthday() { return this.birthday; }
}

