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

    private int star5;
    private int star4;
    private int star3;
    private int star2;
    private int star1;


    public PopularGoodsCursorAdapter(Context context, int step, int max) {
        super(context);
        star4 = max-step;
        star3 = star4-step;
        star2 = star3-step;
        star1 = star2- step;
    }

    private class ViewHolder {
        TextView tv_name;
        ImageView iv_status;
        RatingBar rb_rating;
    }

    public void setValues(){

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
        if (mSelection == cursor.getPosition()) {
            view.setBackgroundColor(context.getResources().getColor(R.color.divider));
        } else {
            view.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
        }
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
        float stars = 1;
        if(pop>star1){
            stars++;
            if(pop>star2){
                stars++;
                if(pop>star3){
                    stars++;
                    if(pop>star4){
                        stars++;
                    }
                }
            }
        }
        return stars;
    }

}
