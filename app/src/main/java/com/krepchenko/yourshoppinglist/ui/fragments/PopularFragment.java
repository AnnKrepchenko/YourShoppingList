package com.krepchenko.yourshoppinglist.ui.fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;
import com.krepchenko.yourshoppinglist.ui.activity.MainActivity;
import com.krepchenko.yourshoppinglist.ui.adapters.PopularGoodsCursorAdapter;
import com.krepchenko.yourshoppinglist.utils.ContextAlert;
import com.krepchenko.yourshoppinglist.utils.UriSegment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ann on 13.02.2016.
 */
public class PopularFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PopularGoodsCursorAdapter(getActivity());
        countScope();
        setHasOptionsMenu(true);
    }


    private void countScope() {
        int max = query(UriSegment.max_popular.toString(), "MAX");
        int avg = query(UriSegment.avg_popular.toString(), "AVG");
        adapter.setScope((max - avg) / 5, max);
        Log.e("step", (max - avg) / 5 + "  " + max);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.pop_context_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clean_popular:
                ContentValues good = new ContentValues();
                good.put(GoodsEntity.POPULARITY, 0);
                getActivity().getContentResolver().update(GoodsEntity.CONTENT_URI, good, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(R.string.empty_fragment_popular);
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    private int query(String pathSegment, String columnSuffix) {
        int result = 0;
        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, pathSegment);
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(cursor.getColumnIndex(columnSuffix + "(" + GoodsEntity.POPULARITY + ")"));
        }
        cursor.close();
        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = adapter.getCursor();
        ContentValues good = new ContentValues();
        String goodName = cursor.getString(cursor.getColumnIndex(GoodsEntity.NAME));
        String status = cursor.getString(cursor.getColumnIndex(GoodsEntity.STATUS));
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        if (status.equals(GoodsEntity.Status.GENERAL.toString())) {
            String dateLastBought = cursor.getString(cursor.getColumnIndex(GoodsEntity.DATE_LAST_BOUGHT));
            if (dateLastBought.equals(currentDate)) {
                ((MainActivity) getActivity()).showAlert(ContextAlert.SURE_BUY, id, goodName);
            } else {
                good.put(GoodsEntity.STATUS, GoodsEntity.Status.TOBUY.toString());
                Toast.makeText(getActivity(), cursor.getString(cursor.getColumnIndex(GoodsEntity.NAME)) + getString(R.string.toast_item_add),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (status.equals(GoodsEntity.Status.TOBUY.toString())) {
            good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
        } else if (status.equals(GoodsEntity.Status.BOUGHT.toString())) {
            good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
        }
        int popularity = cursor.getInt(cursor.getColumnIndex(GoodsEntity.POPULARITY));
        popularity++;
        good.put(GoodsEntity.NUMBER, 0);
        good.put(GoodsEntity.POPULARITY, popularity);
        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(id)));
        getActivity().getContentResolver().update(uri, good, null, null);
        setNotification(false);
        countScope();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, UriSegment.popular.toString());
        return new CursorLoader(getActivity(), uri, null, null, null, GoodsEntity.POPULARITY + " DESC");
    }

}
