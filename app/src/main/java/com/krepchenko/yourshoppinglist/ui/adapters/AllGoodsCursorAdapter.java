package com.krepchenko.yourshoppinglist.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.monxalo.android.widget.SectionCursorAdapter;
import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;

/**
 * Created by Ann on 25.02.2016.
 */
public class AllGoodsCursorAdapter extends SectionCursorAdapter {

    private boolean showNum;
    protected int mSelection = -1;

    protected Context context;
    private String[] categories;
    protected final LayoutInflater inflater;


    public AllGoodsCursorAdapter(Context context, boolean showNum) {
        super(context, null, android.R.layout.preference_category, 2);
        this.showNum = showNum;
        categories = context.getResources().getStringArray(R.array.categories);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_number;
        ImageView iv_status;
    }

    @Override
    protected String getCustomGroup(String groupData) {
        return categories[Integer.parseInt(groupData)];
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        int textColumn = cursor.getColumnIndex(GoodsEntity.NAME);
        holder.tv_name.setText(cursor.getString(textColumn));
        String status = cursor.getString(cursor.getColumnIndex(GoodsEntity.STATUS));
        if (status.equals(GoodsEntity.Status.GENERAL.toString())) {
            holder.iv_status.setBackgroundResource(R.drawable.buy_icon);
        } else if (status.equals(GoodsEntity.Status.TOBUY.toString())) {
            holder.iv_status.setBackgroundResource(R.drawable.return_icon);
        } else if (status.equals(GoodsEntity.Status.BOUGHT.toString())) {
            holder.iv_status.setBackgroundResource(R.drawable.bougth_icon);
        }
        if (mSelection == cursor.getPosition()) {
            view.setBackgroundColor(context.getResources().getColor(R.color.divider));
        } else {
            view.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
        }
        if (showNum) {
            int numberColumn = cursor.getColumnIndex(GoodsEntity.NUMBER);
            if ((cursor.getInt(numberColumn)) == 0)
                holder.tv_number.setText("");
            else
                holder.tv_number.setText(String.valueOf((cursor.getInt(numberColumn))));
        }
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        View result;
        result = inflater.inflate(R.layout.good, parent, false);
        holder.tv_name = (TextView) result.findViewById(R.id.good_name);
        holder.iv_status = (ImageView) result.findViewById(R.id.good_check);
        if (showNum) {
            holder.tv_number = (TextView) result.findViewById(R.id.good_number);
        }
        result.setTag(holder);
        return result;
    }

    public void setNewSelection(int position) {
        mSelection = position;
        notifyDataSetChanged();
    }

    public Integer getCurrentCheckedPosition() {
        return mSelection;
    }

    public String getCurrentString(long id) {
        Cursor cursor = context.getContentResolver().query(GoodsEntity.CONTENT_URI, new String[]{GoodsEntity.NAME}, GoodsEntity._ID + "=?", new String[]{Long.toString(id)}, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(GoodsEntity.NAME));
        }
        return "";
    }

    public void clearSelection() {
        mSelection = -1;
        notifyDataSetChanged();
    }

}
