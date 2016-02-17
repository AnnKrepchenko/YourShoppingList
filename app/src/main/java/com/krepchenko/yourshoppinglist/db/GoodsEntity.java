package com.krepchenko.yourshoppinglist.db;


import android.net.Uri;


public interface GoodsEntity {

	 enum Status {
		GENERAL,
		TOBUY,
		BOUGHT
	}
	
	String AUTHORITY = "com.krepchenko.yourshoppinglist.provider";

	String TABLE_NAME = "all_goods";
	
	Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
	
	String _ID = "_id";
	String NAME = "name";
	String STATUS = "status";
	String POPULARITY = "popularity";
	String NUMBER = "number";
	String DATE_LAST_BOUGHT = "date_last_bought";
	
	String CREATE_SCRIPT = "CREATE TABLE " + TABLE_NAME + " (" +
											_ID + " INTEGER PRIMARY KEY, " +
											NAME + " TEXT NOT NULL," +
											STATUS +" TEXT NOT NULL," +
											POPULARITY + " INTEGER, " +
											NUMBER + " INTEGER," +
											DATE_LAST_BOUGHT + " TEXT)";
	
}
