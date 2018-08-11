package com.iceteaviet.fastfoodfinder.ui.store;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.iceteaviet.fastfoodfinder.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Created by taq on 3/11/2016.
 */

public class NoticeDialog extends DialogFragment {

    private static final String MESSAGE = "message";


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
        String message = "";

        if (getArguments() != null)
            message = getArguments().getString(MESSAGE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NoticeDialogListener listener = (NoticeDialogListener) getActivity();
                if (listener != null) {
                    listener.onClickOk();
                }
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
