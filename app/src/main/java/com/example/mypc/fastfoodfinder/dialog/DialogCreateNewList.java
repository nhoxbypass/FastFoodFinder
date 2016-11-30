package com.example.mypc.fastfoodfinder.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/30/2016.
 */
public class DialogCreateNewList extends DialogFragment {

    ImageView ivQuit;
    Button btnDone;
    EditText edtDescription;
    EditText edtName;
    OnCreateListListener mListener;

    public static DialogCreateNewList newInstance() {
        DialogCreateNewList frag = new DialogCreateNewList();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public interface OnCreateListListener{
        void OnButtonClick(String name, String description);
    }

    public void setOnButtonClickListener (OnCreateListListener listener){
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_create_new_list,container,false);
        ivQuit = (ImageView) rootView.findViewById(R.id.ivQuit);
        btnDone = (Button) rootView.findViewById(R.id.btnDone);
        edtDescription = (EditText) rootView.findViewById(R.id.edtDescription);
        edtName = (EditText) rootView.findViewById(R.id.edtName);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtName.getText().length() < 1){
                    Toast.makeText(getContext(),"Name is not empty!",Toast.LENGTH_SHORT).show();
                }
                else {
                    mListener.OnButtonClick(edtName.getText().toString(), edtDescription.getText().toString());
                    dismiss();
                }
            }
        });


    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //request
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
