package com.example.mytasktimerapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFragment";
    public static final String DATE_PICKER_ID = "ID";
    public static final String DATE_PICKER_TITLE = "TITLE";
    public static final String DATE_PICKER_DATE = "DATE";
    int mDialogId = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final GregorianCalendar cal = new GregorianCalendar();
        String title = null;
        Bundle arguments = getArguments();
        if(arguments != null){
            mDialogId = arguments.getInt(DATE_PICKER_ID);
            title = arguments.getString(DATE_PICKER_TITLE);
            Date givenDate = (Date) arguments.getSerializable(DATE_PICKER_DATE);
            if(givenDate != null){
                cal.setTime(givenDate);
                Log.d(TAG, "onCreateDialog: retrieved date = " + givenDate.toString());
            }
        }
        int year = cal.get(GregorianCalendar.YEAR);
        int month = cal.get(GregorianCalendar.MONTH);
        int day = cal.get(GregorianCalendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getContext(),this,year,month,day);
        if(title != null){
            dpd.setTitle(title);
        }
        return dpd;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(!(context instanceof DatePickerDialog.OnDateSetListener)){
            throw new ClassCastException(context.toString() + " must implement DatePickerDialog.OnDateSetListener interface");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet: entering");
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();
        if(listener != null){
            view.setTag(mDialogId);
            listener.onDateSet(view,year,month,dayOfMonth);
        }
        Log.d(TAG, "onDateSet: existing");
    }
}
