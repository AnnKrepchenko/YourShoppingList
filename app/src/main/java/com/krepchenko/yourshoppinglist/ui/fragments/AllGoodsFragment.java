package com.krepchenko.yourshoppinglist.ui.fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.SearchView;
import android.widget.Toast;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;
import com.krepchenko.yourshoppinglist.ui.activity.MainActivity;
import com.krepchenko.yourshoppinglist.ui.adapters.GoodsCursorAdapter;
import com.krepchenko.yourshoppinglist.utils.ContextAlert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ann on 13.02.2016.
 */
public class AllGoodsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private String mSearchText = "";
    private SearchView mSearchview;
    private static final String KEY_SAVE_SEARCH_TEXT = "search_text";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GoodsCursorAdapter(getActivity());
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            mSearchText = savedInstanceState.getString(KEY_SAVE_SEARCH_TEXT);
        }
        callback = new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.all_goods_context_menu, menu);
                return true;
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        ((MainActivity)getActivity()).showAlert(ContextAlert.EDIT, id, adapter.getCurrentString(id));
                        mode.finish();
                        return true;
                    case R.id.action_delete:
                        ((MainActivity)getActivity()).showAlert(ContextAlert.DELETE, id, adapter.getCurrentString(id));
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


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem item = menu.add("Search").setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        mSearchview = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
        if (mSearchview != null) {
            mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    mSearchText = newText;
                    getLoaderManager().restartLoader(0, null, AllGoodsFragment.this);
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    hideKeyboard(getActivity());
                    return true;
                }
            });
        }
        if (!mSearchText.equals("")) {
            mSearchview.setQuery(mSearchText, true);
        }
        item.setActionView(mSearchview);
        super.onCreateOptionsMenu(menu, inflater);

    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (actionMode != null) {
            checkSelectedId(position, id);
        } else {
            Cursor cursor = adapter.getCursor();
            ContentValues good = new ContentValues();
            String goodName = cursor.getString(cursor.getColumnIndex(GoodsEntity.NAME));
            String status = cursor.getString(cursor.getColumnIndex(GoodsEntity.STATUS));
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            if (status.equals(GoodsEntity.Status.GENERAL.toString())) {
                String dateLastBought = cursor.getString(cursor.getColumnIndex(GoodsEntity.DATE_LAST_BOUGHT));
                if (dateLastBought.equals(currentDate)) {
                    ((MainActivity)getActivity()).showAlert(ContextAlert.SURE_BUY, id, goodName);
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSearchview = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if(mSearchText!=null && !mSearchText.isEmpty()){
            selection = GoodsEntity.NAME + " LIKE ?" +" OR " +GoodsEntity.NAME+ " LIKE ?";
            selectionArgs = new String[]{"%"+ mSearchText+"%","%"+Character.toUpperCase(mSearchText.charAt(0)) + mSearchText.substring(1) + "%"};
        }
        return new CursorLoader(getActivity(), GoodsEntity.CONTENT_URI, null, selection,selectionArgs, GoodsEntity.NAME + " COLLATE LOCALIZED ASC");
    }

}
