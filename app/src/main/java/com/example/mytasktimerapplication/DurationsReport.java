package com.example.mytasktimerapplication;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DurationsReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener, AppDialog.DialogEvents, View.OnClickListener {

    public static final String TAG = "DurationsReport";
    public static final String SELECTION_PARAM = "SELECTION";
    public static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    public static final String SORT_ORDER_PARAM = "SORT_ORDER";
    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String DISPLAY_WEEK = "DISPLAY_WEEK";
    public static final String DELETION_DATE = "DELETION_DATE";
    private static final int LOADER_ID = 1;
    public static final int DIALOG_FILTER = 1;
    public static final int DIALOG_DELETE = 2;

    private Bundle mArgs = new Bundle();
    private boolean mDisplayWeek = true;
    private DurationsRVAdapter mAdapter;
    private final GregorianCalendar mCalender = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_durations_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if(savedInstanceState != null){
            long timeInMillis = savedInstanceState.getLong(CURRENT_DATE,0);
            if(timeInMillis != 0){
                mCalender.setTimeInMillis(timeInMillis);
                mCalender.clear(GregorianCalendar.HOUR_OF_DAY);
                mCalender.clear(GregorianCalendar.MINUTE);
                mCalender.clear(GregorianCalendar.SECOND);
            }
            mDisplayWeek = savedInstanceState.getBoolean(DISPLAY_WEEK,true);
        }
        applyFilter();
        TextView taskName = findViewById(R.id.td_name_heading);
        taskName.setOnClickListener(this);
        TextView taskDec = findViewById(R.id.td_description_heading);
        if(taskDec != null){
            taskDec.setOnClickListener(this);
        }
        TextView taskDate = findViewById(R.id.td_start_heading);
        taskDate.setOnClickListener(this);
        TextView taskDuration = findViewById(R.id.td_duration_heading);
        taskDuration.setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.td_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(mAdapter == null){
            mAdapter = new DurationsRVAdapter(this,null);
        }
        recyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(LOADER_ID,mArgs,this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: called");
        switch (v.getId()){
            case R.id.td_name_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationsContract.columns.DURATIONS_NAME);
                break;
            case R.id.td_description_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationsContract.columns.DURATIONS_DESCRIPTION);
                break;
            case R.id.td_start_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationsContract.columns.DURATIONS_START_DATE);
                break;
            case R.id.td_duration_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationsContract.columns.DURATIONS_DURATIONS);
                break;
        }
        getSupportLoaderManager().restartLoader(LOADER_ID,mArgs,this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_DATE,mCalender.getTimeInMillis());
        outState.putBoolean(DISPLAY_WEEK,mDisplayWeek);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.rm_filter_period:
                mDisplayWeek = !mDisplayWeek;
                applyFilter();
                invalidateOptionsMenu();
                getSupportLoaderManager().restartLoader(LOADER_ID,mArgs,this);
                return true;
            case R.id.rm_filter_date:
                showDatePickerDialog("Select date for report", DIALOG_FILTER);
                return true;
            case R.id.rm_delete:
                showDatePickerDialog("Select date to delete upto ", DIALOG_DELETE);
                 return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = findViewById(R.id.rm_filter_period);
        if(item != null){
          if(mDisplayWeek){
              item.setIcon(R.drawable.ic_baseline_filter_1_24);
              item.setTitle("Show Day");
          }else{
              item.setIcon(R.drawable.ic_baseline_filter_7_24);
              item.setTitle("Show week");
          }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showDatePickerDialog(String title, int dialogId){
        Log.d(TAG, "showDatePickerDialog: entering");
        DialogFragment dialogFragment = new DatePickerFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(DatePickerFragment.DATE_PICKER_ID,dialogId);
        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE,title);
        arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE,mCalender.getTime());
        dialogFragment.setArguments(arguments);
        dialogFragment.show(getSupportFragmentManager(),"datePicker");
        Log.d(TAG, "showDatePickerDialog: exiting");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet: called");
        int dialogId = (int) view.getTag();
        switch (dialogId){
            case DIALOG_FILTER:
                mCalender.set(year,month,dayOfMonth,0,0,0);
                applyFilter();
                getSupportLoaderManager().restartLoader(LOADER_ID,mArgs,this);
                break;
            case DIALOG_DELETE:
                mCalender.set(year,month,dayOfMonth,0,0,0);
                String fromDate = android.text.format.DateFormat.getDateFormat(this).format(mCalender.getTimeInMillis());
                AppDialog dialog = new AppDialog();
                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID,1);
                args.putString(AppDialog.DIALOG_MESSAGE,"Are you sure you want to delete all timings before " + fromDate + "?");
                args.putLong(DELETION_DATE,mCalender.getTimeInMillis());
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(),null);
                break;
            default:
                throw new IllegalArgumentException("Invalid mode when recieving DatePicker Dialog Result");
        }
    }

    private void deleteRecords(long timeInMillis){
        Log.d(TAG, "deleteRecords: entering");
        long longDate =timeInMillis/1000;
        String[] selectionArgs = new String[]{Long.toString(longDate)};
        String selection = TimingsContract.columns.TIMINGS_START_TIME + " < ?";
        Log.d(TAG, "deleteRecords: prior to " + longDate);
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(TimingsContract.CONTENT_URI,selection,selectionArgs);
        applyFilter();
        getSupportLoaderManager().restartLoader(LOADER_ID,mArgs,this);
        Log.d(TAG, "deleteRecords: exiting");
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        long deleteDate = args.getLong(DELETION_DATE);
        deleteRecords(deleteDate);
        getSupportLoaderManager().restartLoader(LOADER_ID,mArgs,this);
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }

    private void applyFilter(){
        Log.d(TAG, "applyFilter: entering");
        if(mDisplayWeek){
            Date currentCalenderDate = mCalender.getTime();
            int dayOfWeek = mCalender.get(GregorianCalendar.DAY_OF_WEEK);
            int weekStart = mCalender.getFirstDayOfWeek();
            Log.d(TAG, "applyFilter: first day of calender week is " + weekStart);
            Log.d(TAG, "applyFilter: dayOfWeek is " + dayOfWeek);
            Log.d(TAG, "applyFilter: data is " + mCalender.getTime());

            mCalender.set(GregorianCalendar.DAY_OF_WEEK,weekStart);
            String startDate = String.format(Locale.US,"%04d-%02d-%02d",mCalender.get(GregorianCalendar.YEAR),mCalender.get(GregorianCalendar.MONTH)+1,
                    mCalender.get(GregorianCalendar.DAY_OF_MONTH));
            mCalender.add(GregorianCalendar.DATE,6);
            String endDate = String.format(Locale.US,"%04d-%02d-%02d",mCalender.get(GregorianCalendar.YEAR),mCalender.get(GregorianCalendar.MONTH)+1,
                    mCalender.get(GregorianCalendar.DAY_OF_MONTH));
            String[] selectionArgs = new String[]{startDate,endDate};
            mCalender.setTime(currentCalenderDate);
            Log.d(TAG, "applyFilter: Start date is " + startDate + " End date is " + endDate);
            mArgs.putString(SELECTION_PARAM," StartDate Between ? AND ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM,selectionArgs);
        }else{
            String startDate = String.format(Locale.US,"%04d-%02d-%02d",mCalender.get(GregorianCalendar.YEAR),mCalender.get(GregorianCalendar.MONTH)+1,
                                 mCalender.get(GregorianCalendar.DAY_OF_MONTH));
            String[] selectionArgs = new String[]{startDate};
            Log.d(TAG, "applyFilter: Start date is " + startDate);
            mArgs.putString(SELECTION_PARAM," StartDate = ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM,selectionArgs);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch(id){
            case LOADER_ID:
                String[] projection = {BaseColumns._ID,
                                       DurationsContract.columns.DURATIONS_NAME,
                                       DurationsContract.columns.DURATIONS_DESCRIPTION,
                                       DurationsContract.columns.DURATIONS_START_TIME,
                                       DurationsContract.columns.DURATIONS_START_DATE,
                                       DurationsContract.columns.DURATIONS_DURATIONS};

                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if(args != null){
                    selection = args.getString(SELECTION_PARAM);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
                    sortOrder = args.getString(SORT_ORDER_PARAM);
                }
                return new CursorLoader(this,DurationsContract.CONTENT_URI,projection,selection,selectionArgs,sortOrder);

            default:
                throw new InvalidParameterException(TAG + " .oncreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished");
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();

        Log.d(TAG, "onLoadFinished: count is " + count);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);
    }


}