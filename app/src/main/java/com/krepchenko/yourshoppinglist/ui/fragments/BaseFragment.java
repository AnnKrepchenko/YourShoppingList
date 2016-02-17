package com.krepchenko.yourshoppinglist.ui.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;
import com.krepchenko.yourshoppinglist.ui.activity.MainActivity;
import com.krepchenko.yourshoppinglist.ui.adapters.BaseCursorAdapter;
import com.krepchenko.yourshoppinglist.utils.ContextAlert;

/**
 * Created by Ann on 13.02.2016.
 */
public abstract class BaseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {



    private static String KEY_ID = "id";
    private static String KEY_NAME = "name";
    private static String KEY_ALERT = "alert";

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.fragment_listview);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
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
            adapter.setNewSelection(position);
            actionMode = getActivity().startActionMode(callback);
            this.id = id;
        } else {
            deleteSelection();
        }
        return true;
    }

    protected void setSnackBar(View view, String text, String action) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(action, null).show();
    }


    protected void setNotification(boolean isFirst){
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
        //if (!data.isClosed())
        adapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
