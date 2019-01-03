package com.parse.starter;

import android.graphics.Bitmap;
import android.location.Location;

public class Grapp {

    String title;
    String desc;
    String id;
    Bitmap img;
    Location location;

    public Grapp(String title, String desc, Bitmap img, String id, Location location) {
        this.title = title;
        this.desc = desc;
        this.id = id;
        this.img = img;
        this.location = location;
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
}

