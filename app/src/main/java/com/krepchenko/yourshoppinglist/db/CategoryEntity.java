package com.krepchenko.yourshoppinglist.db;

import android.net.Uri;

/**
 * Created by Ann on 23.02.2016.
 */
public interface CategoryEntity {
    String AUTHORITY = "com.krepchenko.yourshoppinglist.provider";

    String TABLE_NAME = "categories";

    Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    String _ID = "_id";
    String NAME = "name";
    String COLOR = "color";

    String CREATE_SCRIPT = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY, " +
            NAME + " TEXT NOT NULL, " +
            COLOR + " TEXT NOT NULL)";
}
