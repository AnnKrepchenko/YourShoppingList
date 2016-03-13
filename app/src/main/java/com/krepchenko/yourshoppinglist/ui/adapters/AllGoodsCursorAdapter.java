package com.krepchenko.yourshoppinglist.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.CategoryEntity;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;
import com.krepchenko.yourshoppinglist.ui.fragments.AllGoodsFragment;

/**
 * Created by Ann on 25.02.2016.
 */
public class AllGoodsCursorAdapter extends CursorTreeAdapter {

    protected int selectionGroup = -1;
    protected int selectionChild = -1;

    protected Context context;
    protected final LayoutInflater inflater;
    private String filter = "";

    public AllGoodsCursorAdapter(Context context, AllGoodsFragment fragment) {
        super(null, context);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        String selection = GoodsEntity.CATEGORY_ID + "=? AND (" + GoodsEntity.NAME + " LIKE ?" + " OR " + GoodsEntity.NAME + " LIKE ?)";
        String[] selectionArgs = new String[]{groupCursor.getString(groupCursor.getColumnIndex(CategoryEntity._ID)),"%" + filter + "%",(filter.isEmpty()) ? "%" + filter + "%" : "%" + Character.toUpperCase(filter.charAt(0)) + filter.substring(1) + "%"};
        Cursor cursor = context.getContentResolver().query(GoodsEntity.CONTENT_URI, null, selection, selectionArgs, GoodsEntity.NAME + " ASC");
        return cursor;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        if (!filter.isEmpty()) {
            mExpandableListView.expandGroup(groupPosition);
        }else {
          //  mExpandableListView.collapseGroup(groupPosition);
        }
        return v;
    }

    @Override
    public View newGroupView(Context context, Cursor cursor,
                             boolean isExpanded, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.good, parent, false);
        return view;
    }

    @Override
    public void bindGroupView(View view, Context context, Cursor cursor,
                              boolean isExpanded) {

        TextView lblListHeader = (TextView) view
                .findViewById(R.id.good_name);
        if (selectionChild == -1 && selectionGroup == cursor.getPosition()) {
            view.setBackgroundColor(context.getResources().getColor(R.color.accent));
        } else {
            view.setBackgroundColor(Color.parseColor(cursor.getString(cursor.getColumnIndex(CategoryEntity.COLOR))));
        }
        if (lblListHeader != null) {
            lblListHeader.setText(cursor.getString(cursor
                    .getColumnIndex(CategoryEntity.NAME)));
        }
    }

    private class ViewHolder {
        TextView tv_name;
        ImageView iv_status;
    }

    @Override
    public void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
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
        if (selectionChild == cursor.getPosition() && selectionGroup == getCursor().getPosition()) {
            view.setBackgroundColor(context.getResources().getColor(R.color.divider));
        } else {
            view.setBackground(null);
        }
    }

    @Override
    public View newChildView(Context context, final Cursor cursor,
                             boolean isLastChild, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        View result;
        result = inflater.inflate(R.layout.good, parent, false);
        holder.tv_name = (TextView) result.findViewById(R.id.good_name);
        holder.iv_status = (ImageView) result.findViewById(R.id.good_check);
        result.setTag(holder);
        return result;
    }

    public void setNewSelection(int positionGroup, int positionChild) {
        selectionChild = positionChild;
        selectionGroup = positionGroup;
        notifyDataSetChanged();
    }

    public Integer getCurrentCheckedPositionChild() {
        return selectionChild;
    }

    public Integer getCurrentCheckedPositionGroup() {
        return selectionGroup;
    }

    public String getCurrentName(int group, int child) {
        return getChild(group, child).getString(getChild(group, child).getColumnIndex(GoodsEntity.NAME));
    }

    public long getCurrentChildId(int group, int child) {
        return getChild(group, child).getLong(getChild(group, child).getColumnIndex(GoodsEntity._ID));
    }

    public String getCurrentGroupName(int group) {
        return getGroup(group).getString(getCursor().getColumnIndex(CategoryEntity.NAME));
    }

    public long getCurrentGroupId(int group) {
        return getGroup(group).getLong(getCursor().getColumnIndex(CategoryEntity._ID));
    }

    public void clearSelection() {
        selectionChild = -1;
        selectionGroup = -1;
        notifyDataSetChanged();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String selection = GoodsEntity.CATEGORY_ID + "=? AND (" + GoodsEntity.NAME + " LIKE ?" + " OR " + GoodsEntity.NAME + " LIKE ?)";
        String[] selectionArgs = new String[]{getGroup(groupPosition).getString(getGroup(groupPosition).getColumnIndex(CategoryEntity._ID)),"%" + filter + "%",(filter.isEmpty()) ?"%" + filter + "%" : "%" + Character.toUpperCase(filter.charAt(0)) + filter.substring(1) + "%"};
        Cursor cursor = context.getContentResolver().query(GoodsEntity.CONTENT_URI, null, selection, selectionArgs, GoodsEntity.NAME + " ASC");
        return cursor.getCount();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
    }

    @Override
    public void setGroupCursor(Cursor cursor) {

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public void setFilter(String filter) {
        this.filter = filter;
        notifyDataSetChanged();
    }
}
