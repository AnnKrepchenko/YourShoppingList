package com.krepchenko.yourshoppinglist.ui.fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.CategoryEntity;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;
import com.krepchenko.yourshoppinglist.ui.activity.MainActivity;
import com.krepchenko.yourshoppinglist.ui.adapters.AllGoodsCursorAdapter;
import com.krepchenko.yourshoppinglist.utils.ContextAlert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ann on 13.02.2016.
 */
public class AllGoodsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener ,ViewTreeObserver.OnScrollChangedListener{

    private String mSearchText = "";
    private ActionMode actionMode;
    private ActionMode.Callback callback;
    private SearchView mSearchview;
    private static final String KEY_SAVE_SEARCH_TEXT = "search_text";
    private AllGoodsCursorAdapter adapter;
    private ExpandableListView listView;
    private int groupCount=0;
    private int child = -1;
    private int group = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new AllGoodsCursorAdapter(getActivity(), this);
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
                        if (child == -1) {
                            Log.i("Click", "allgoods: action edit group");
                            ((MainActivity) getActivity()).showAlert(ContextAlert.EDIT_CATEGORY, 0, adapter.getCurrentGroupName(group), adapter.getCurrentGroupId(group));

                        } else {
                            Log.i("Click", "allgoods: action edit child");
                            ((MainActivity) getActivity()).showAlert(ContextAlert.EDIT_GOOD, adapter.getCurrentChildId(group, child), adapter.getCurrentName(group, child), group);
                        }
                        mode.finish();
                        return true;
                    case R.id.action_delete:
                        if (child == -1) {
                            Log.i("Click", "allgoods: action delete group");
                            ((MainActivity) getActivity()).showAlert(ContextAlert.DELETE_CATEGORY, 0, adapter.getCurrentGroupName(group), adapter.getCurrentGroupId(group));

                        } else {
                            Log.i("Click", "allgoods: action delete child");
                            ((MainActivity) getActivity()).showAlert(ContextAlert.DELETE_GOOD, adapter.getCurrentChildId(group, child), adapter.getCurrentName(group, child), 0);
                        }
                        adapter.notifyDataSetChanged();
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
        return inflater.inflate(R.layout.fragment_pinned_section_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = (ExpandableListView) view.findViewById(R.id.fragment_listview);
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        listView.setOnChildClickListener(this);
        listView.setOnGroupClickListener(this);
      //  listView.setOnScrollChangeListener();
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            Log.i("Longclick", "child " + position);
            group = ExpandableListView.getPackedPositionGroup(id);
            child = ExpandableListView.getPackedPositionChild(id);
            if (actionMode == null) {
                adapter.setNewSelection(group, child);
                actionMode = getActivity().startActionMode(callback);
            } else {
                deleteSelection();
            }
        } else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            Log.i("Longclick", "group " + position);
            group = ExpandableListView.getPackedPositionGroup(id);
            child = ExpandableListView.getPackedPositionChild(id);

            if (actionMode == null) {
                adapter.setNewSelection(group, child);
                actionMode = getActivity().startActionMode(callback);
            } else {
                deleteSelection();
            }
        }
        return true;
    }

    private void deleteSelection() {
        this.id = -1;
        adapter.clearSelection();
        actionMode.finish();
    }


    protected void checkSelectedId(int group, int child) {
        if (this.group != group || this.child != child) {
            adapter.setNewSelection(group, child);
            this.group = group;
            this.child = child;
        } else {
            deleteSelection();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem item = menu.add("Search").setIcon(R.drawable.ic_magnify_white_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        mSearchview = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
        if (mSearchview != null) {
            mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("Search", newText);
                    mSearchText = newText;
                    adapter.setFilter(mSearchText);
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    hideKeyboard(getActivity());
                    return true;
                }
            });
        }
        mSearchview.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.i("Search","onClose");
                for (int i=0;i<groupCount;i++){
                    Log.i("Search","collapse group " + i);
                    listView.collapseGroup(i);
                }
                return false;
            }
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        mSearchview = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), CategoryEntity.CONTENT_URI, null, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        groupCount = data.getCount();
        adapter.changeCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (actionMode != null) {
            Log.i("Click", "action mode active, child: child " + childPosition + " group " + groupPosition);
            checkSelectedId(groupPosition, childPosition);
        } else {
            Log.i("Click", " child: child " + childPosition + " group " + groupPosition);
            Cursor cursor = adapter.getChild(groupPosition, childPosition);
            ContentValues good = new ContentValues();
            int good_id = cursor.getInt(cursor.getColumnIndex(GoodsEntity._ID));
            String goodName = cursor.getString(cursor.getColumnIndex(GoodsEntity.NAME));
            String status = cursor.getString(cursor.getColumnIndex(GoodsEntity.STATUS));
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            if (status.equals(GoodsEntity.Status.GENERAL.toString())) {
                String dateLastBought = cursor.getString(cursor.getColumnIndex(GoodsEntity.DATE_LAST_BOUGHT));
                if (dateLastBought.equals(currentDate)) {
                    ((MainActivity) getActivity()).showAlert(ContextAlert.SURE_BUY, good_id, goodName, 0);
                } else {
                    good.put(GoodsEntity.STATUS, GoodsEntity.Status.TOBUY.toString());
                    setSnackBar(v, cursor.getString(cursor.getColumnIndex(GoodsEntity.NAME)) + getString(R.string.toast_item_add), null);
                }
            } else if (status.equals(GoodsEntity.Status.TOBUY.toString())) {
                good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
            } else if (status.equals(GoodsEntity.Status.BOUGHT.toString())) {
                good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
            }
            good.put(GoodsEntity.NUMBER, 0);
            Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(good_id)));
            getActivity().getContentResolver().update(uri, good, null, null);
            setNotification(false);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        if (actionMode != null) {
            Log.i("Click", "action mode active, group: group " + groupPosition);
            checkSelectedId(groupPosition, -1);
            return true;
        } else {
            Log.i("Click", "group: group " + groupPosition);
            return false;
        }
    }

    @Override
    public void onScrollChanged() {

    }
}
