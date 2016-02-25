package com.krepchenko.yourshoppinglist.ui.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;
import com.krepchenko.yourshoppinglist.ui.fragments.AllGoodsFragment;
import com.krepchenko.yourshoppinglist.ui.fragments.MyListFragment;
import com.krepchenko.yourshoppinglist.ui.fragments.PopularFragment;
import com.krepchenko.yourshoppinglist.utils.ContextAlert;
import com.krepchenko.yourshoppinglist.utils.TextUtils;

import java.util.Stack;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static String KEY_ITEM = "item";
    private static String KEY_ID = "id";
    private static String KEY_NAME = "name";
    private static String KEY_ALERT = "alert";

    private FloatingActionButton fab;
    private NavigationView navigationView;
    private int selectedItem = 0;


    private long goodId;
    private String goodName;
    private ContextAlert contextAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerview.findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSnackBar(v,"Ну не работает, че тыкать то??",null);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState != null) {
            selectDrawerItem(navigationView.getMenu().getItem(savedInstanceState.getInt(KEY_ITEM)));
            if (savedInstanceState.containsKey(KEY_ALERT)) {
                showAlert((ContextAlert) savedInstanceState.getSerializable(KEY_ALERT), savedInstanceState.getLong(KEY_ID), savedInstanceState.getString(KEY_NAME));
            }
        } else {
            selectDrawerItem(navigationView.getMenu().getItem(0));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_ITEM, selectedItem);
        if (contextAlert != null) {
            outState.putSerializable(KEY_ALERT, contextAlert);
            outState.putLong(KEY_ID, goodId);
            outState.putString(KEY_NAME, goodName);
        }
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return selectDrawerItem(item);
    }

    private boolean selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        if (id == R.id.nav_my_list) {
            fragmentClass = MyListFragment.class;
            fab.setVisibility(View.VISIBLE);
            selectedItem = 0;
        } else if (id == R.id.nav_all_goods) {
            selectedItem = 1;
            fab.setVisibility(View.VISIBLE);
            fragmentClass = AllGoodsFragment.class;
        } else if (id == R.id.nav_popular) {
            fab.setVisibility(View.GONE);
            fragmentClass = PopularFragment.class;
        } else if (id == R.id.nav_share) {
            sendMyList();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        item.setChecked(true);
        setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void setSnackBar(View view, String text, String action) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(action, null).show();
    }

    @Override
    public void onClick(View v) {
        switch (selectedItem) {
            case 0:
                cleanBought();
                setSnackBar(v, getString(R.string.toast_list_cleaned), null);
                break;
            case 1:
                showAlert(ContextAlert.ADD, 0, null);
                break;
        }
    }

    private void cleanBought() {
        String[] projection = new String[]{GoodsEntity.STATUS};
        String selection = GoodsEntity.STATUS + " = ?";
        String[] selectionArgs = new String[]{GoodsEntity.Status.BOUGHT.toString()};
        Cursor cursor = getContentResolver().query(GoodsEntity.CONTENT_URI, projection, selection, selectionArgs, null);
        ContentValues good = new ContentValues();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
            getContentResolver().update(GoodsEntity.CONTENT_URI, good, selection, selectionArgs);
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void cleanSaving() {
        this.goodId = 0;
        this.goodName = null;
        this.contextAlert = null;
    }

    public void showAlert(ContextAlert which, final long goodId, final String goodName) {
        this.goodId = goodId;
        this.goodName = goodName;
        this.contextAlert = which;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        final View view_et = View.inflate(this, R.layout.dialog_fragment_et, null);
        final View view_np = View.inflate(this, R.layout.dialog_fragment_np, null);
        final EditText input = (EditText) view_et.findViewById(R.id.et_dialog);
        final TextInputLayout til_dialog = (TextInputLayout)view_et.findViewById(R.id.til_dialog);
        til_dialog.setErrorEnabled(false);
        final NumberPicker nPicker = (NumberPicker) view_np.findViewById(R.id.number_picker);
        switch (which) {
            case DELETE: {
                alertDialog.setTitle(R.string.alert_dialog_del_title);
                alertDialog.setMessage(getString(R.string.alert_dialog_del_mess) + goodName + "?");
                alertDialog.setIcon(R.drawable.delete_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(goodId)));
                        getContentResolver().delete(uri, null, null);
                        cleanSaving();
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.alert_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanSaving();
                    }
                });
                alertDialog.show();
            }
            break;
            case SURE_BUY: {
                alertDialog.setTitle(R.string.alert_dialog_buy_title);
                alertDialog.setMessage(goodName + getString(R.string.alert_dialog_buy_mess));
                alertDialog.setIcon(R.drawable.edit_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_add_to_mylist), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues good = new ContentValues();
                        good.put(GoodsEntity.STATUS, GoodsEntity.Status.TOBUY.toString());
                        setSnackBar(getCurrentFocus(), goodName + getString(R.string.toast_item_add), null);
                        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(goodId)));
                        getContentResolver().update(uri, good, null, null);
                        setNotification(false);
                        cleanSaving();
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.alert_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanSaving();
                    }
                });
                alertDialog.show();
            }
            break;
            case EDIT: {
                alertDialog.setTitle(R.string.dialog_fr_edit_title);
                alertDialog.setMessage(R.string.alert_dialog_edit_mess);
                alertDialog.setView(view_et);
                input.setText(goodName);
                alertDialog.setIcon(R.drawable.edit_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_save), null);
                alertDialog.setNegativeButton(getString(R.string.alert_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanSaving();
                    }
                });
                final AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                ContentValues good = new ContentValues();
                                String name = input.getText().toString();
                                if (TextUtils.checkNameForStartSpecSymbols(name) ) {
                                    if(!checkGoodExist(name,goodId)) {
                                        good.put(GoodsEntity.NAME, name.trim());
                                        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(goodId)));
                                        getContentResolver().update(uri, good, null, null);
                                        cleanSaving();
                                        alertDialog1.dismiss();
                                    }else{
                                        til_dialog.setError(getString(R.string.alert_error_name_exist));
                                    }
                                } else {
                                    til_dialog.setError(getString(R.string.alert_error_name_symbols));
                                }
                            }
                        });
                    }
                });
                alertDialog1.show();
            }
            break;
            case ADD: {
                alertDialog.setTitle(R.string.alert_dialog_add_title);
                alertDialog.setMessage(R.string.alert_dialog_add_mess);
                alertDialog.setView(view_et);
                input.setText(goodName);
                alertDialog.setIcon(R.drawable.add_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_save), null);
                alertDialog.setNegativeButton(getString(R.string.alert_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanSaving();
                    }
                });
                final AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                ContentValues good = new ContentValues();
                                String name = input.getText().toString();
                                if (TextUtils.checkNameForStartSpecSymbols(name)) {
                                    if(!checkGoodExist(name,-1)) {
                                        good.put(GoodsEntity.NAME, name.trim());
                                        good.put(GoodsEntity.CATEGORY_ID,0);
                                        good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
                                        good.put(GoodsEntity.POPULARITY, 0);
                                        good.put(GoodsEntity.DATE_LAST_BOUGHT, "");
                                        getContentResolver().insert(GoodsEntity.CONTENT_URI, good);
                                        cleanSaving();
                                        alertDialog1.dismiss();
                                    }else{
                                        til_dialog.setError(getString(R.string.alert_error_name_exist));
                                    }
                                } else {
                                    til_dialog.setError(getString(R.string.alert_error_name_symbols));
                                }
                            }
                        });
                    }
                });
                alertDialog1.show();
            }
            break;
            case SET_NUMBER: {
                alertDialog.setTitle(R.string.dialog_fr_set_number_title);
                nPicker.setMaxValue(99);
                nPicker.setMinValue(1);
                alertDialog.setView(view_np);

                alertDialog.setIcon(R.drawable.edit_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues good = new ContentValues();
                        good.put(GoodsEntity.NUMBER, nPicker.getValue());
                        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(goodId)));
                        getContentResolver().update(uri, good, null, null);
                        cleanSaving();
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.alert_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanSaving();
                    }
                });
                alertDialog.show();
            }
            break;
            default:
                break;
        }
    }

    private boolean checkGoodExist(String goodName,long goodId){
        goodName = goodName.trim();
        Cursor cursor = getContentResolver().query(GoodsEntity.CONTENT_URI,null,"("+GoodsEntity.NAME + "=? OR "+ GoodsEntity.NAME + "=?) AND "+ GoodsEntity._ID + "<>?", new String[]{Character.toLowerCase(goodName.charAt(0)) + goodName.substring(1),Character.toUpperCase(goodName.charAt(0)) + goodName.substring(1),String.valueOf(goodId)},null);
        return  cursor.moveToFirst();
    }

    private void sendMyList() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, getMyList());
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_my_list));
        i = Intent.createChooser(i, getString(R.string.send_my_list));
        startActivity(i);
    }

    private String getMyList() {
        String myList = getString(R.string.send_my_list_start_str);
        String selection = GoodsEntity.STATUS + "=?";
        String[] selectionArgs = new String[]{GoodsEntity.Status.TOBUY.toString()};
        Cursor cursor = getContentResolver().query(GoodsEntity.CONTENT_URI, null, selection, selectionArgs, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            myList = getString(R.string.send_my_list_empty);
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                if (i != 0) {
                    myList = myList + ", ";
                    cursor.moveToNext();
                }
                myList = myList + cursor.getString(cursor.getColumnIndex(GoodsEntity.NAME));
                if (cursor.getInt(cursor.getColumnIndex(GoodsEntity.NUMBER)) != 0)
                    myList = myList + " " + cursor.getInt(cursor.getColumnIndex(GoodsEntity.NUMBER));
            }
        }
        cursor.close();
        return myList;
    }


    public void setNotification(Boolean firstStart) {
        NotifyTask task = new NotifyTask();
        task.execute(firstStart);
    }


    class NotifyTask extends AsyncTask<Boolean, Void, Integer> {

        private Boolean firstStart;

        @Override
        protected Integer doInBackground(Boolean... params) {
            int count;
            for (Boolean param : params) {
                firstStart = param;
            }
            String[] projection = new String[]{GoodsEntity.NAME};
            String selection = GoodsEntity.STATUS + " = ?";
            String[] selectionArgs = new String[]{GoodsEntity.Status.TOBUY.toString()};
            Cursor cursor = getContentResolver().query(GoodsEntity.CONTENT_URI, projection, selection, selectionArgs, null);
            count = cursor.getCount();
            cursor.close();
            return count;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            int mId = 0;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this).setSmallIcon(R.drawable.ic_launcher);
            Intent resultIntent = new Intent(MainActivity.this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (result > 0) {
                mBuilder.setContentTitle(String.valueOf(result) + getString(R.string.notification_goods_remaining));
                mNotificationManager.notify(mId, mBuilder.build());
            } else {
                if (!firstStart) {
                    mBuilder.setContentTitle(getString(R.string.notification_shopping_finished));
                    mNotificationManager.notify(mId, mBuilder.build());
                }
            }
        }
    }
}
