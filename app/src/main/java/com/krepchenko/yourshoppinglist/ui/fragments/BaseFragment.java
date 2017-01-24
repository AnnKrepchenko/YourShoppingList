package com.krepchenko.yourshoppinglist.ui.fragments;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.ui.activity.MainActivity;
import com.krepchenko.yourshoppinglist.ui.adapters.BaseCursorAdapter;
import com.krepchenko.yourshoppinglist.utils.TextUtils;

/**
 * Created by Ann on 13.02.2016.
 */
public abstract class BaseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,View.OnClickListener {


    protected ActionMode actionMode;
    protected ActionMode.Callback callback;
    protected BaseCursorAdapter adapter;
    protected ListView listView;
    protected long id = -1;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.fragment_listview);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextUtils.hideKeyboard(getActivity());

    }

    protected void checkSelectedId(int position, long id) {
        if (this.id != id) {
            adapter.setNewSelection(position);
        } else {
            deleteSelection();
        }
    }

    private void deleteSelection() {
        this.id = -1;
        adapter.clearSelection();
        actionMode.finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (actionMode == null) {
            Log.i("Click", "action mode active, item " + position);
            adapter.setNewSelection(position);
            actionMode = getActivity().startActionMode(callback);
            this.id = id;
        } else {
            Log.i("Click", "action mode disactive, item " + position);
            deleteSelection();
        }
        return true;
    }

    protected void setSnackBar(View view, String text, String action) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(action, null).show();
    }


    protected void setNotification(boolean isFirst){
        Log.i("Notification", "notification start, isFirst " + isFirst);
        ((MainActivity)getActivity()).setNotification(isFirst);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (actionMode != null)
            actionMode.finish();
    }

    protected void setProgressDialog(String text){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(text);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void cancelProgressDialog(){
        progressDialog.dismiss();
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    protected void setEmptyText(int textId){
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.view_empty,null);
        emptyView.findViewById(R.id.empty_iv).setOnClickListener(this);
        ((TextView)emptyView.findViewById(R.id.empty_text)).setText(textId);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.empty_iv:
                Log.i("Click", "empty cat");
                setSnackBar(v,"Meow ^_^",null);
                break;
        }
    }
}
