package com.krepchenko.yourshoppinglist.ui.fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;
import com.krepchenko.yourshoppinglist.ui.activity.MainActivity;
import com.krepchenko.yourshoppinglist.ui.adapters.GoodsCursorAdapter;
import com.krepchenko.yourshoppinglist.utils.ContextAlert;
import com.krepchenko.yourshoppinglist.utils.UriSegment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ann on 13.02.2016.
 */
public class MyListFragment extends BaseFragment implements AbsListView.OnScrollListener {

    private static final int LOADER_ID = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GoodsCursorAdapter(getActivity(), true);
        callback = new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.my_list_context_menu, menu);
                return true;
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_set_number:
                        Log.i("Click", "set number");
                        ((MainActivity) getActivity()).showAlert(ContextAlert.SET_NUMBER, id, adapter.getCurrentString(id), 0);
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            public void onDestroyActionMode(ActionMode mode) {
                adapter.clearSelection();
                actionMode = null;
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(R.string.empty_fragment_my_list);
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(LOADER_ID, Bundle.EMPTY, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, UriSegment.my_list.toString());
        return new CursorLoader(getActivity(), uri, null, null, null, GoodsEntity.NAME
                + " COLLATE LOCALIZED ASC");
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID,Bundle.EMPTY,this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (actionMode != null) {
            Log.i("Click","action mode active, "+position);
            checkSelectedId(position, id);
        } else {
            Log.i("Click", "My list goods item clicked " + position);
            Cursor cursor = adapter.getCursor();
            cursor.moveToPosition(position);
            ContentValues good = new ContentValues();
            String status = cursor.getString(cursor.getColumnIndex(GoodsEntity.STATUS));
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            if (status.equals(GoodsEntity.Status.GENERAL.toString())) {
                good.put(GoodsEntity.STATUS, GoodsEntity.Status.TOBUY.toString());
            } else if (status.equals(GoodsEntity.Status.TOBUY.toString())) {
                good.put(GoodsEntity.DATE_LAST_BOUGHT, currentDate);
                good.put(GoodsEntity.STATUS, GoodsEntity.Status.BOUGHT.toString());
            } else if (status.equals(GoodsEntity.Status.BOUGHT.toString())) {
                good.put(GoodsEntity.NUMBER, 0);
                good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
            }
            int popularity = cursor.getInt(cursor.getColumnIndex(GoodsEntity.POPULARITY));
            popularity++;
            good.put(GoodsEntity.POPULARITY, popularity);
            Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(id)));
            getActivity().getContentResolver().update(uri, good, null, null);
            setNotification(false);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;
        if(getActivity()!=null) {
            if (lastItem == totalItemCount && totalItemCount > visibleItemCount) {
                ((MainActivity) getActivity()).hideFam();
            } else {
                ((MainActivity) getActivity()).showFam();
            }
        }
    }

}