package com.krepchenko.yourshoppinglist.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.widget.CursorAdapter;

import com.krepchenko.yourshoppinglist.db.GoodsEntity;

/**
 * Created by Ann on 15.02.2016.
 */
public abstract class BaseCursorAdapter extends CursorAdapter {

    protected int mSelection=-1;

    protected Context context;

    protected final LayoutInflater inflater;

    public BaseCursorAdapter(Context context) {
        super(context, null, true);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setNewSelection(int position) {
        mSelection = position;
        notifyDataSetChanged();
    }

    public String getCurrentString(long id){
        Cursor cursor = context.getContentResolver().query(GoodsEntity.CONTENT_URI, new String[]{GoodsEntity.NAME}, GoodsEntity._ID + "=?", new String[]{Long.toString(id)}, null);
        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(GoodsEntity.NAME));
        }
        return "";
    }

    public void clearSelection() {
        mSelection = -1;
        notifyDataSetChanged();
    }

    abstract public void setScope( int step, int max);

}
