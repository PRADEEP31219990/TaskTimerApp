package com.example.mytasktimerapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class AppDialog extends DialogFragment {
    private static final String TAG = "AppDialog";
    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    interface DialogEvents{
        void onPositiveDialogResult(int dialogId, Bundle args);
        void onNegativeDialogResult(int dialogId, Bundle args);
        void onDialogCancelled(int dialogId);
    }

    private DialogEvents mDialogEvents;

    @Override
    public void onAttach( Context context) {
        Log.d(TAG, "onAttach: Entering onAttach Activity is " + context.toString());
        super.onAttach(context);
        if(!(context instanceof DialogEvents)){
            throw new ClassCastException(context.toString() + " must implement AppDailog. Appdialog Interface");
        }
        mDialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: Entering");
        super.onDetach();
        mDialogEvents = null;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Log.d(TAG, "onCreateDialog: starts");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Bundle arguments = getArguments();
        int dialogId = 0;
        String messageString = null;
        int positiveStringId = 0;
        int negativeStringId = 0;

        if(arguments != null){
            dialogId = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);

            if(dialogId == 0 || messageString == null){
                throw new IllegalArgumentException("DIALOG_ID Or DIALOG_MEESAGE not present in the bundle");
            }

            if(positiveStringId == 0){
                positiveStringId = R.string.ok;
            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);
            if(negativeStringId == 0){
                negativeStringId = R.string.cancel;
            }
        }else{
            throw new IllegalArgumentException("Must pass DialogId and Dialog Message in the bundle");
        }

        int finalDialogId = dialogId;
        int finalDialogId1 = dialogId;
        builder.setMessage(messageString).setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDialogEvents != null) {
                    mDialogEvents.onPositiveDialogResult(finalDialogId, arguments);
                }
            }
        }).setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mDialogEvents != null) {
                    mDialogEvents.onNegativeDialogResult(finalDialogId1, arguments);
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onCancel: called");
        if(mDialogEvents != null){
            int dialogId = getArguments().getInt(DIALOG_ID);
            mDialogEvents.onDialogCancelled(dialogId);
        }
    }


}
