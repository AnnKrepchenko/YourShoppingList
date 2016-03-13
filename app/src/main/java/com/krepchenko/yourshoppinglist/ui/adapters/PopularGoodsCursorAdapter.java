package com.krepchenko.yourshoppinglist.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;

/**
 * Created by Ann on 15.02.2016.
 */
public class PopularGoodsCursorAdapter extends BaseCursorAdapter {

    private int avg;
    private int step;


    public PopularGoodsCursorAdapter(Context context) {
        super(context);
    }

    private class ViewHolder {
        TextView tv_name;
        ImageView iv_status;
        RatingBar rb_rating;
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
        holder.rb_rating.setRating(getRatingValue(cursor.getInt(cursor.getColumnIndex(GoodsEntity.POPULARITY))));
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        View result;
        result = inflater.inflate(R.layout.popular_good, parent, false);
        holder.tv_name = (TextView) result.findViewById(R.id.good_name);
        holder.iv_status = (ImageView) result.findViewById(R.id.good_check);
        holder.rb_rating = (RatingBar) result.findViewById(R.id.good_rating);
        result.setTag(holder);
        return result;
    }

    private float getRatingValue(int pop){
        return (pop-avg)/step;
    }

    public void setScope( int step, int avg){
        this.step = step>0 ? step : 1;
        this.avg = avg;
        notifyDataSetChanged();
    }
}
