package com.krepchenko.yourshoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.krepchenko.yourshoppinglist.R;

public class DbHelper extends SQLiteOpenHelper {

    private static final String NAME = "goods_db";
    private static final int VERSION = 2;
    private Context context;

    public DbHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GoodsEntity.CREATE_SCRIPT);
        db.execSQL(CategoryEntity.CREATE_SCRIPT);
        String[] categories = context.getResources().getStringArray(R.array.categories);
        String[] food = context.getResources().getStringArray(R.array.food);
        String[] personalHygiene = context.getResources().getStringArray(R.array.personal_hygiene);
        String[] pets = context.getResources().getStringArray(R.array.products_for_animals);
        String[] chem = context.getResources().getStringArray(R.array.household_products);
        String[] other = context.getResources().getStringArray(R.array.other);
        int i = 0;
        for (String categoryItem : categories) {
            db.execSQL("INSERT INTO " + CategoryEntity.TABLE_NAME + " VALUES(" + i + ",'" + categoryItem + "')");
            i++;
        }
        i=0;
        for (String foodItem : food) {
            insertItemScript(db, i, foodItem, 1);
            i++;
        }
        for (String personalHygieneItem : personalHygiene) {
            insertItemScript(db, i, personalHygieneItem, 2);
            i++;
        }
        for (String chemItem : chem) {
            insertItemScript(db, i, chemItem, 3);
            i++;
        }
        for (String petItem : pets) {
            insertItemScript(db, i, petItem, 4);
            i++;
        }
        for (String otherItem : other) {
            insertItemScript(db, i, otherItem, 5);
            i++;
        }
    }

    private void insertItemScript(SQLiteDatabase db, int i, String name, int categoryId) {
        db.execSQL("INSERT INTO " + GoodsEntity.TABLE_NAME + " VALUES(" + i + ",'" + name + "'," + categoryId + ",'GENERAL',0,0,0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

}
