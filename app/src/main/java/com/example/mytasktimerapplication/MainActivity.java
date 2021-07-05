 package com.example.mytasktimerapplication;

//import android.content.ContentResolver;
//import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
//import android.database.Cursor;
import android.content.res.Configuration;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
//import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mytasktimerapplication.debug.TestData;

 public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
        AddEditActivityFragment.OnSaveClicked,AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";


    private boolean mTwoPane = false;

    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;
    private AlertDialog mDialog = null;
    public static final int DIALOG_ID_CANCEL_EDIT_UP = 3;
    private Timing mCurrentTiming = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTwoPane = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        Log.d(TAG, "onCreate: twoPane is " + mTwoPane);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Boolean editing = fragmentManager.findFragmentById(R.id.task_details_container) != null;
        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.fragment);
        if(mTwoPane){
            Log.d(TAG, "onCreate: twoPane mode");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.VISIBLE);
        }else if(editing){
            Log.d(TAG, "onCreate: single pane, editing");
            mainFragment.setVisibility(View.GONE);
        }else{
            Log.d(TAG, "onCreate: single pane not editing");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(BuildConfig.DEBUG){
            MenuItem generate = menu.findItem(R.id.menumain_generate);
            generate.setVisible(true);
        }
        return true;
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.fragment);
        if(!mTwoPane){
            addEditLayout.setVisibility(View.GONE);
            mainFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.menumain_showDurations:
                startActivity(new Intent(this,DurationsReport.class));
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                showAboutDialog();
                break;
            case R.id.menumain_generate:
                TestData.generateTestData(getContentResolver());
                break;
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                AddEditActivityFragment fragment = (AddEditActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                if(fragment.canClose()){
                    return super.onOptionsItemSelected(item);
                }else{
                    showConfirmationDialog(DIALOG_ID_CANCEL_EDIT_UP);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAboutDialog(){
        View messageView = getLayoutInflater().inflate(R.layout.about,null,false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("TaskTimer");
        builder.setView(messageView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();
                }
            }
        });
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Entering messageView.onClick , showing = " + mDialog.isShowing());
                if(mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();
                }
            }
        });
        TextView tv = messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);
        mDialog.show();
    }

    @Override
    public void onEditClick(Task task) {
       taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: starts");
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE,getString(R.string.deldiag_message,task.getId(),task.getmName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID,R.string.delete_task_data);
        args.putLong("TaskId",task.getId());
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);
    }

    private void taskEditRequest(Task task) {
        Log.d(TAG, "taskEditRequest: starts");
        Log.d(TAG, "taskEditRequest: in two-pane mode (tablet)");
        AddEditActivityFragment fragment = new AddEditActivityFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(Task.class.getSimpleName(),task);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.task_details_container,fragment).commit();
        if(!mTwoPane){
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone)");
            View mainFragment = findViewById(R.id.fragment);
            View addEditLayout = findViewById(R.id.task_details_container);
            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "taskEditRequest: Existing taskEditRequest");
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        switch(dialogId){
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("TaskId");
                if(BuildConfig.DEBUG && taskId == 0)  throw new AssertionError("Task id is zero");
                getContentResolver().delete(TasksContract.buildTaskUri(taskId),null,null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                break;
        }

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
                break;
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
                if(fragment != null){
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    if(mTwoPane){
                        if(dialogId == DIALOG_ID_CANCEL_EDIT){
                            finish();
                        }
                    }else {
                        View addEditLayout = findViewById(R.id.task_details_container);
                        View mainFragment = findViewById(R.id.fragment);
                        addEditLayout.setVisibility(View.GONE);
                        mainFragment.setVisibility(View.VISIBLE);
                    }
                }else{
                    finish();
                }
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.task_details_container);
        if((fragment == null) || fragment.canClose()){
            super.onBackPressed();
        }else{
            showConfirmationDialog(DIALOG_ID_CANCEL_EDIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if((mDialog != null) && (mDialog.isShowing())){
            mDialog.dismiss();
        }
    }

    public void showConfirmationDialog(int dialogId){
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, dialogId);
        args.putString(AppDialog.DIALOG_MESSAGE,getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID,R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID,R.string.cancelEditDiag_negative_caption);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);
    }

    @Override
    public void onTaskLongClick(Task task) {

    }
}

