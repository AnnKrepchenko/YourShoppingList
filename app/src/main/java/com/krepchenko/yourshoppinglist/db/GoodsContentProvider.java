package com.krepchenko.yourshoppinglist.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class GoodsContentProvider extends ContentProvider {

    private DbHelper dbHelper;
    private static final int GOODS = 1;
    private static final int GOODS_GOOD = 2;
    private static final int GOODS_MYLIST = 3;
    private static final int GOODS_POPULAR = 4;
    private static final int GOODS_POPULAR_AVG = 5;
    private static final int GOODS_POPULAR_MAX = 6;
    //--
    private static final int CATEGORIES = 7;
    private static final int CATEGORIES_CATEGORY = 8;
    //-
    private static final int ALL_GOODS= 9;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, GoodsEntity.TABLE_NAME, GOODS);
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, GoodsEntity.TABLE_NAME + "/#", GOODS_GOOD);
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, GoodsEntity.TABLE_NAME + "/my_list", GOODS_MYLIST);
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, GoodsEntity.TABLE_NAME + "/popular", GOODS_POPULAR);
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, GoodsEntity.TABLE_NAME + "/avg_popular", GOODS_POPULAR_AVG);
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, GoodsEntity.TABLE_NAME + "/max_popular", GOODS_POPULAR_MAX);
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, CategoryEntity.TABLE_NAME, CATEGORIES);
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, CategoryEntity.TABLE_NAME + "/#", CATEGORIES_CATEGORY);
        URI_MATCHER.addURI(GoodsEntity.AUTHORITY, CategoryEntity.TABLE_NAME + "/all_goods", ALL_GOODS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String selectionToAppend = "";
        String tableName;
        String query = "";
        switch (URI_MATCHER.match(uri)) {
            case GOODS:
                tableName = GoodsEntity.TABLE_NAME;
                break;
            case GOODS_GOOD:
                tableName = GoodsEntity.TABLE_NAME;
                selectionToAppend = GoodsEntity._ID + " = " + uri.getLastPathSegment();
                break;
            case GOODS_MYLIST:
                tableName = GoodsEntity.TABLE_NAME;
                selection = GoodsEntity.STATUS + "=? OR " + GoodsEntity.STATUS + "=?";
                selectionArgs = new String[]{GoodsEntity.Status.TOBUY.toString(), GoodsEntity.Status.BOUGHT.toString()};
                break;
            case GOODS_POPULAR:
                tableName = GoodsEntity.TABLE_NAME;
                selection = GoodsEntity.POPULARITY + ">" + "  ( SELECT AVG(" + GoodsEntity.POPULARITY + ") FROM " + tableName + ") ";
                break;
            case GOODS_POPULAR_AVG:
                tableName = GoodsEntity.TABLE_NAME;
                query = "SELECT AVG(" + GoodsEntity.POPULARITY + ") FROM " + tableName;
                break;
            case GOODS_POPULAR_MAX:
                tableName = GoodsEntity.TABLE_NAME;
                query = "SELECT MAX(" + GoodsEntity.POPULARITY + ") FROM " + tableName;
                break;
            case CATEGORIES:
                tableName = CategoryEntity.TABLE_NAME;
                break;
            case CATEGORIES_CATEGORY:
                tableName = CategoryEntity.TABLE_NAME;
                selectionToAppend = CategoryEntity._ID + " = " + uri.getLastPathSegment();
                break;
            case ALL_GOODS:
                tableName = CategoryEntity.TABLE_NAME;
                query = "SELECT *"+/*GoodsEntity.TABLE_NAME+"."+ GoodsEntity.NAME + ", "+ CategoryEntity.TABLE_NAME+"."+CategoryEntity.NAME + */" FROM " + GoodsEntity.TABLE_NAME + ", "+ CategoryEntity.TABLE_NAME + " WHERE " + GoodsEntity.TABLE_NAME+"."+GoodsEntity.CATEGORY_ID + "=" + CategoryEntity.TABLE_NAME+"."+CategoryEntity._ID;
                break;
            default:
                throw new IllegalArgumentException("Wrong uri");
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c;
        if(URI_MATCHER.match(uri)==GOODS_POPULAR_AVG || URI_MATCHER.match(uri)==GOODS_POPULAR_MAX || URI_MATCHER.match(uri)==ALL_GOODS){
             c = db.rawQuery(query,null);
        }else {
            c = db.query(tableName, projection, appendSelections(selection, selectionToAppend), selectionArgs, null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }


    @Override
    public String getType(Uri uri) {
        String result;
        switch (URI_MATCHER.match(uri)) {
            case GOODS:
                result = "vnd.android.cursor.dir/com.krepchenko.yourshoppinglist.provider." + GoodsEntity.TABLE_NAME;
                break;
            case GOODS_GOOD:
                result = "vnd.android.cursor.item/com.krepchenko.yourshoppinglist.provider." + GoodsEntity.TABLE_NAME;
                break;
            case CATEGORIES:
                result = "vnd.android.cursor.dir/com.krepchenko.yourshoppinglist.provider." + CategoryEntity.TABLE_NAME;
                break;
            case CATEGORIES_CATEGORY:
                result = "vnd.android.cursor.item/com.krepchenko.yourshoppinglist.provider." + CategoryEntity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long result;
        String tableName;
        switch (URI_MATCHER.match(uri)) {
            case GOODS:
                tableName = GoodsEntity.TABLE_NAME;
                break;
            case CATEGORIES:
                tableName = CategoryEntity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        result = db.insert(tableName, null, values);
        notifyAllUri();
        return Uri.withAppendedPath(uri, String.valueOf(result));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName;
        String selectionToAppend = "";
        switch (URI_MATCHER.match(uri)) {
            case GOODS:
                tableName = GoodsEntity.TABLE_NAME;
                break;
            case GOODS_GOOD:
                tableName = GoodsEntity.TABLE_NAME;
                selectionToAppend = GoodsEntity._ID + " = " + uri.getLastPathSegment();
                break;
            case GOODS_POPULAR:
                tableName = GoodsEntity.TABLE_NAME;
                selection = GoodsEntity.POPULARITY + ">" + "  ( SELECT AVG(" + GoodsEntity.POPULARITY + ") FROM " + tableName + ") ";
                break;
            case CATEGORIES:
                tableName = CategoryEntity.TABLE_NAME;
                break;
            case CATEGORIES_CATEGORY:
                tableName = CategoryEntity.TABLE_NAME;
                selectionToAppend = CategoryEntity._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affected = db.delete(tableName, appendSelections(selection, selectionToAppend), selectionArgs);
        if (affected > 0) {
            notifyAllUri();
        }
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName;
        String selectionToAppend = "";
        switch (URI_MATCHER.match(uri)) {
            case GOODS:
                tableName = GoodsEntity.TABLE_NAME;
                break;
            case GOODS_GOOD:
                tableName = GoodsEntity.TABLE_NAME;
                selectionToAppend = GoodsEntity._ID + " = " + uri.getLastPathSegment();
                break;
            case GOODS_POPULAR:
                tableName = GoodsEntity.TABLE_NAME;
                selection = GoodsEntity.POPULARITY + ">" + "  ( SELECT AVG(" + GoodsEntity.POPULARITY + ") FROM " + tableName + ") ";
                break;
            case CATEGORIES:
                tableName = CategoryEntity.TABLE_NAME;
                break;
            case CATEGORIES_CATEGORY:
                tableName = CategoryEntity.TABLE_NAME;
                selectionToAppend = CategoryEntity._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affected = db.update(tableName, values, appendSelections(selection, selectionToAppend), selectionArgs);
        if (affected > 0) {
            notifyAllUri();
        }
        return affected;
    }

    private void notifyAllUri() {
        String uriAll = "content://com.krepchenko.yourshoppinglist.provider/";
        getContext().getContentResolver().notifyChange(Uri.parse(uriAll), null);
    }

    private static String appendSelections(String baseSelection, String selectionToAppend) {
        if (!TextUtils.isEmpty(selectionToAppend)) {
            if (!TextUtils.isEmpty(baseSelection)) {
                baseSelection = " ( " + baseSelection + " ) AND " + selectionToAppend;
            } else {
                baseSelection = selectionToAppend;
            }
        }
        return baseSelection;
    }

}
