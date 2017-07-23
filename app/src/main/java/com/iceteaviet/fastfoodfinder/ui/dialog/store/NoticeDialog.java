package com.iceteaviet.fastfoodfinder.ui.dialog.store;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by taq on 3/11/2016.
 */

public class NoticeDialog extends DialogFragment {

    private static final String MESSAGE = "message";

    public NoticeDialog() {
    }

    public static NoticeDialog newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        NoticeDialog fragment = new NoticeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(MESSAGE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NoticeDialogListener listener = (NoticeDialogListener) getActivity();
                if (listener != null) {
                    listener.onClickOk();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }

    public interface NoticeDialogListener {
        void onClickOk();
    }
}
