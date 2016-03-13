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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.krepchenko.yourshoppinglist.R;
import com.krepchenko.yourshoppinglist.db.CategoryEntity;
import com.krepchenko.yourshoppinglist.db.GoodsEntity;
import com.krepchenko.yourshoppinglist.ui.fragments.AllGoodsFragment;
import com.krepchenko.yourshoppinglist.ui.fragments.MyListFragment;
import com.krepchenko.yourshoppinglist.ui.fragments.PopularFragment;
import com.krepchenko.yourshoppinglist.utils.ContextAlert;
import com.krepchenko.yourshoppinglist.utils.TextUtils;

import java.util.Random;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static String KEY_ITEM = "item";
    private static String KEY_ID = "id";
    private static String KEY_CATEGORY_ID = "category_id";
    private static String KEY_NAME = "name";
    private static String KEY_ALERT = "alert";

    private FloatingActionsMenu fam;
    private FloatingActionButton fabAddGood;
    private FloatingActionButton fabAddCategory;
    private FloatingActionButton fabCleanList;
    private FloatingActionButton fabSendList;
    private NavigationView navigationView;
    private int selectedItem = 0;


    private long goodId;
    private long categotyId;
    private String goodName;
    private ContextAlert contextAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fam = (FloatingActionsMenu) findViewById(R.id.fam);
        /*CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fam.getLayoutParams();
        p.setBehavior(new ScrollAwareFABBehavior(this,null));
        fam.setLayoutParams(p);*/
        fabAddGood = (FloatingActionButton) findViewById(R.id.fab_add_good);
        fabAddGood.setColorPressed(R.color.accent);
        fabAddGood.setOnClickListener(this);
        fabAddCategory = (FloatingActionButton) findViewById(R.id.fab_add_category);
        fabAddCategory.setColorPressed(R.color.accent);
        fabAddCategory.setOnClickListener(this);
        fabCleanList = (FloatingActionButton) findViewById(R.id.fab_clean_bought);
        fabCleanList.setColorPressed(R.color.accent);
        fabCleanList.setOnClickListener(this);
        fabSendList = (FloatingActionButton) findViewById(R.id.fab_send_list);
        fabSendList.setColorPressed(R.color.accent);
        fabSendList.setOnClickListener(this);
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
                setSnackBar(v, getEmojiByUnicode(0x1F601) + " <- смотри че могу", null);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState != null) {
            selectDrawerItem(navigationView.getMenu().getItem(savedInstanceState.getInt(KEY_ITEM)));
            if (savedInstanceState.containsKey(KEY_ALERT)) {
                Log.i("MainActivity", "saved alert");
                showAlert((ContextAlert) savedInstanceState.getSerializable(KEY_ALERT), savedInstanceState.getLong(KEY_ID), savedInstanceState.getString(KEY_NAME), savedInstanceState.getLong(KEY_CATEGORY_ID));
            }
        } else {
            selectDrawerItem(navigationView.getMenu().getItem(0));
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
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
            outState.putLong(KEY_CATEGORY_ID, categotyId);
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
        fam.collapse();
        if (id == R.id.nav_my_list) {
            fragmentClass = MyListFragment.class;
            fabAddCategory.setVisibility(View.GONE);
            fabAddGood.setVisibility(View.GONE);
            fabSendList.setVisibility(View.VISIBLE);
            fabCleanList.setVisibility(View.VISIBLE);
            fam.setVisibility(View.VISIBLE);
            selectedItem = 0;
            Log.i("Drawer", fragmentClass.getName());
        } else if (id == R.id.nav_all_goods) {
            selectedItem = 1;
            fabAddCategory.setVisibility(View.VISIBLE);
            fabAddGood.setVisibility(View.VISIBLE);
            fabSendList.setVisibility(View.GONE);
            fabCleanList.setVisibility(View.GONE);
            fam.setVisibility(View.VISIBLE);
            fragmentClass = AllGoodsFragment.class;
            Log.i("Drawer", fragmentClass.getName());
        } else if (id == R.id.nav_popular) {
            fam.setVisibility(View.GONE);
            fragmentClass = PopularFragment.class;
            Log.i("Drawer", fragmentClass.getName());
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

    private void cleanBought() {
        String selection = GoodsEntity.STATUS + " = ?";
        String[] selectionArgs = new String[]{GoodsEntity.Status.BOUGHT.toString()};
        ContentValues good = new ContentValues();
        good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
        getContentResolver().update(GoodsEntity.CONTENT_URI, good, selection, selectionArgs);
    }

    private void cleanSaving() {
        this.goodId = 0;
        this.goodName = null;
        this.contextAlert = null;
    }

    public void showAlert(ContextAlert which, final long goodId, final String name, final long categoryId) {
        this.goodId = goodId;
        this.goodName = name;
        this.categotyId = categoryId;
        this.contextAlert = which;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        final View view_et = View.inflate(this, R.layout.dialog_fragment_et, null);
        final View view_np = View.inflate(this, R.layout.dialog_fragment_np, null);
        final View view_sp = View.inflate(this, R.layout.dialog_fragment_sp_ed, null);
        final EditText input_category = (EditText) view_et.findViewById(R.id.et_dialog);
        final TextInputLayout til_dialog_category = (TextInputLayout) view_et.findViewById(R.id.til_dialog);
        til_dialog_category.setErrorEnabled(false);
        final TextInputLayout til_dialog_good = (TextInputLayout) view_sp.findViewById(R.id.til_dialog);
        til_dialog_good.setErrorEnabled(false);
        final NumberPicker nPicker = (NumberPicker) view_np.findViewById(R.id.number_picker);
        final Spinner spinner = (Spinner) view_sp.findViewById(R.id.spin_dialog);
        final EditText input_good = (EditText) view_sp.findViewById(R.id.et_dialog);
        switch (which) {
            case DELETE_GOOD: {
                alertDialog.setTitle(R.string.alert_dialog_del_title);
                alertDialog.setMessage(getString(R.string.alert_dialog_del_mess) + name + "?");
                alertDialog.setIcon(R.drawable.delete_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(goodId)));
                        getContentResolver().delete(uri, null, null);
                        Log.i("Alert|Delete good", "deleted good " + name);
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
            case DELETE_CATEGORY: {
                alertDialog.setTitle(R.string.alert_dialog_del_title);
                alertDialog.setMessage(getString(R.string.alert_dialog_del_mess) + name + "?");
                alertDialog.setIcon(R.drawable.delete_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues good = new ContentValues();
                        good.put(GoodsEntity.CATEGORY_ID, 100);
                        getContentResolver().update(GoodsEntity.CONTENT_URI, good, GoodsEntity.CATEGORY_ID + "=?", new String[]{Long.toString(categoryId)});
                        Uri uri = Uri.withAppendedPath(CategoryEntity.CONTENT_URI, Uri.encode(Long.toString(goodId)));
                        getContentResolver().delete(uri, null, null);
                        Log.i("Alert|Delete category", "deleted category " + name);
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
                alertDialog.setMessage(name + getString(R.string.alert_dialog_buy_mess));
                alertDialog.setIcon(R.drawable.edit_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_add_to_mylist), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues good = new ContentValues();
                        good.put(GoodsEntity.STATUS, GoodsEntity.Status.TOBUY.toString());
                        setSnackBar(getCurrentFocus(), name + getString(R.string.toast_item_add), null);
                        Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(goodId)));
                        getContentResolver().update(uri, good, null, null);
                        setNotification(false);
                        Log.i("Alert|Sure buy", "to buy good " + name);
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
            case EDIT_GOOD: {
                alertDialog.setTitle(R.string.dialog_fr_edit_good_title);
                alertDialog.setView(view_sp);
                input_good.setText(name);
                Cursor cursor = getContentResolver().query(CategoryEntity.CONTENT_URI, null, null, null, null);
                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, new String[]{CategoryEntity.NAME},
                        new int[]{android.R.id.text1}, 0);
                spinner.setAdapter(simpleCursorAdapter);
                spinner.setSelection((int) categoryId);
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
                                String name = input_good.getText().toString();
                                if (TextUtils.checkNameForStartSpecSymbols(name)) {
                                    if (!checkGoodExist(name, goodId)) {
                                        if (name.length() > 2) {
                                            good.put(GoodsEntity.NAME, name.trim());
                                            good.put(GoodsEntity.CATEGORY_ID, spinner.getSelectedItemId());
                                            Uri uri = Uri.withAppendedPath(GoodsEntity.CONTENT_URI, Uri.encode(Long.toString(goodId)));
                                            getContentResolver().update(uri, good, null, null);
                                            cleanSaving();
                                            Log.i("Alert|Edit good", "updated good " + name + "to category with id " + spinner.getSelectedItemId());
                                            alertDialog1.dismiss();
                                        } else {
                                            Log.i("Alert|Edit good", "not updated good " + name + "to category with id " + spinner.getSelectedItemId());
                                            til_dialog_good.setError(getString(R.string.alert_error_name_small));
                                        }
                                    } else {
                                        Log.i("Alert|Edit good", "not updated good " + name + "to category with id " + spinner.getSelectedItemId());
                                        til_dialog_good.setError(getString(R.string.alert_error_name_good_exist));
                                    }
                                } else {
                                    Log.i("Alert|Edit good", "not updated good " + name + "to category with id " + spinner.getSelectedItemId());
                                    til_dialog_good.setError(getString(R.string.alert_error_name_symbols));
                                }
                            }
                        });
                    }
                });
                alertDialog1.show();
            }
            break;
            case EDIT_CATEGORY: {
                alertDialog.setTitle(R.string.dialog_fr_edit_category_title);
                alertDialog.setMessage(R.string.alert_dialog_edit_category_mess);
                alertDialog.setView(view_et);
                input_category.setText(name);
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
                                    String name = input_category.getText().toString();
                                    if (TextUtils.checkNameForStartSpecSymbols(name)) {
                                        if (!checkCategoryExist(name, categoryId)) {
                                            if (name.length() > 2) {
                                                good.put(GoodsEntity.NAME, name.trim());
                                                Uri uri = Uri.withAppendedPath(CategoryEntity.CONTENT_URI, Uri.encode(Long.toString(categoryId)));
                                                getContentResolver().update(uri, good, null, null);
                                                cleanSaving();
                                                Log.i("Alert|Edit category", "updated category " + name);
                                                alertDialog1.dismiss();
                                            } else {
                                                Log.i("Alert|Edit category", "not updated category " + name);
                                                til_dialog_category.setError(getString(R.string.alert_error_name_small));
                                            }
                                        } else {
                                            Log.i("Alert|Edit category", "not updated category " + name);
                                            til_dialog_category.setError(getString(R.string.alert_error_name_category_exist));
                                        }
                                    } else {
                                        Log.i("Alert|Edit category", "not updated category " + name);
                                        til_dialog_category.setError(getString(R.string.alert_error_name_symbols));
                                    }
                                }
                            }
                           );
                       }
                   }
                );
                alertDialog1.show();
            }
            break;
            case ADD_GOOD: {
                alertDialog.setTitle(R.string.alert_dialog_add_title);
                alertDialog.setView(view_sp);
                input_good.setText(name);
                Cursor cursor = getContentResolver().query(CategoryEntity.CONTENT_URI, null, null, null, null);
                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, new String[]{CategoryEntity.NAME},
                        new int[]{android.R.id.text1}, 0);
                spinner.setAdapter(simpleCursorAdapter);
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
                                String name = input_good.getText().toString();
                                if (TextUtils.checkNameForStartSpecSymbols(name)) {
                                    if (!checkGoodExist(name, -1)) {
                                        if (name.length() > 2) {
                                            good.put(GoodsEntity.NAME, name.trim());
                                            good.put(GoodsEntity.CATEGORY_ID, spinner.getSelectedItemId());
                                            good.put(GoodsEntity.STATUS, GoodsEntity.Status.GENERAL.toString());
                                            good.put(GoodsEntity.POPULARITY, 0);
                                            good.put(GoodsEntity.DATE_LAST_BOUGHT, "");
                                            getContentResolver().insert(GoodsEntity.CONTENT_URI, good);
                                            cleanSaving();
                                            Log.i("Alert|Add good", "saved good " + name + "to category with id " + spinner.getSelectedItemId());
                                            alertDialog1.dismiss();
                                        } else {
                                            Log.i("Alert|Add good", "not updated good " + name + "to category with id " + spinner.getSelectedItemId());
                                            til_dialog_good.setError(getString(R.string.alert_error_name_small));
                                        }
                                    } else {
                                        Log.i("Alert|Add good", "not saved good " + name + "to category with id " + spinner.getSelectedItemId());
                                        til_dialog_good.setError(getString(R.string.alert_error_name_good_exist));
                                    }
                                } else {
                                    Log.i("Alert|Add good", "not saved good " + name + "to category with id " + spinner.getSelectedItemId());
                                    til_dialog_good.setError(getString(R.string.alert_error_name_symbols));
                                }
                            }
                        });
                    }
                });
                alertDialog1.show();
            }

            break;
            case ADD_CATEGORY:

            {
                alertDialog.setTitle(R.string.alert_dialog_add_category_title);
                alertDialog.setMessage(R.string.alert_dialog_add_category_mess);
                alertDialog.setView(view_et);
                input_category.setText(name);
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
                                ContentValues category = new ContentValues();
                                String name = input_category.getText().toString();
                                if (TextUtils.checkNameForStartSpecSymbols(name)) {
                                    if (!checkCategoryExist(name, -1)) {
                                        if (name.length() > 2) {
                                            category.put(CategoryEntity.NAME, name.trim());
                                            String color = "#33"+generateColor()+generateColor()+generateColor();
                                            category.put(CategoryEntity.COLOR,color);
                                            getContentResolver().insert(CategoryEntity.CONTENT_URI, category);
                                            cleanSaving();
                                            Log.i("Alert|Add category", "saved category " + name + " color " + color);
                                            alertDialog1.dismiss();
                                        } else {
                                            Log.i("Alert|Add category", "not saved category " + name);
                                            til_dialog_category.setError(getString(R.string.alert_error_name_small));
                                        }
                                    } else {
                                        Log.i("Alert|Add category", "not saved category " + name);
                                        til_dialog_category.setError(getString(R.string.alert_error_name_category_exist));
                                    }
                                } else {
                                    Log.i("Alert|Add category", "not saved category " + name);
                                    til_dialog_category.setError(getString(R.string.alert_error_name_symbols));
                                }
                            }
                        });
                    }
                });
                alertDialog1.show();
            }

            break;
            case SET_NUMBER:

            {
                alertDialog.setTitle(R.string.dialog_fr_set_number_title);
                nPicker.setMaxValue(99);
                nPicker.setMinValue(1);
                alertDialog.setView(view_np);
                alertDialog.setIcon(R.drawable.edit_icon);
                alertDialog.setPositiveButton(getString(R.string.alert_dialog_button_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues good = new ContentValues();
                        Log.i("Alert|Set number", "numberPicker value " + nPicker.getValue());
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

    private boolean checkGoodExist(String goodName, long goodId) {
        boolean result;
        goodName = goodName.trim();
        Cursor cursor = getContentResolver().query(GoodsEntity.CONTENT_URI, null, "(" + GoodsEntity.NAME + "=? OR " + GoodsEntity.NAME + "=?) AND " + GoodsEntity._ID + "<>?", new String[]{Character.toLowerCase(goodName.charAt(0)) + goodName.substring(1), Character.toUpperCase(goodName.charAt(0)) + goodName.substring(1), String.valueOf(goodId)}, null);
        result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    private boolean checkCategoryExist(String name, long id) {
        boolean result;
        name = name.trim();
        Cursor cursor = getContentResolver().query(CategoryEntity.CONTENT_URI, null, "(" + CategoryEntity.NAME + "=? OR " + CategoryEntity.NAME + "=?) AND " + CategoryEntity._ID + "<>?", new String[]{Character.toLowerCase(name.charAt(0)) + name.substring(1), Character.toUpperCase(name.charAt(0)) + name.substring(1), String.valueOf(id)}, null);
        result = cursor.moveToFirst();
        cursor.close();
        return result;
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

    private String generateColor(){
        String res = Integer.toHexString(new Random().nextInt(255));
        return res.length()>1 ? res : "0"+res;
    }


    public void setNotification(Boolean firstStart) {
        NotifyTask task = new NotifyTask();
        task.execute(firstStart);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_good:
                Log.i("Click", "fab add good");
                showAlert(ContextAlert.ADD_GOOD, 0, null, 0);
                break;
            case R.id.fab_add_category:
                Log.i("Click", "fab add category");
                showAlert(ContextAlert.ADD_CATEGORY, 0, null, 0);
                break;
            case R.id.fab_clean_bought:
                cleanBought();
                Log.i("Click", "fab clean bought");
                setSnackBar(v, getString(R.string.toast_list_cleaned), null);
                break;
            case R.id.fab_send_list:
                Log.i("Click", "fab send list");
                sendMyList();
                break;
        }
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
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this).setSmallIcon(R.drawable.ic_stat_basket);
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

    public class ScrollAwareFABBehavior extends android.support.design.widget.FloatingActionButton.Behavior {
        public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
            super();
        }

        @Override
        public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout,
                                           final android.support.design.widget.FloatingActionButton child,
                                           final View directTargetChild, final View target, final int nestedScrollAxes) {
            // Ensure we react to vertical scrolling
            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                    || super.onStartNestedScroll(coordinatorLayout, child,
                    directTargetChild, target, nestedScrollAxes);
        }

        @Override
        public void onNestedScroll(final CoordinatorLayout coordinatorLayout,
                                   final android.support.design.widget.FloatingActionButton child,
                                   final View target, final int dxConsumed, final int dyConsumed,
                                   final int dxUnconsumed, final int dyUnconsumed) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed);
            if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
                // User scrolled down and the FAB is currently visible -> hide the FAB
                child.hide();
            } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
                // User scrolled up and the FAB is currently not visible -> show the FAB
                child.show();
            }
        }
    }
}
