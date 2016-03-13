package com.krepchenko.yourshoppinglist.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;

public class GoodsCursorAdapter extends BaseCursorAdapter {

    private boolean showNum;

    public GoodsCursorAdapter(Context context, boolean showNum) {
        super(context);
        this.showNum = showNum;
    }

    @Override
    public void setScope(int step, int max) {

    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_number;
        ImageView iv_status;
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
            view.setBackground(null);
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

}
