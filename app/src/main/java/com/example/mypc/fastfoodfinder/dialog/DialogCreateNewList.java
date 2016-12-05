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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MyPC on 11/30/2016.
 */
public class DialogCreateNewList extends DialogFragment {

    ImageView ivQuit;
    Button btnDone;
    EditText edtName;
    OnCreateListListener mListener;
    ArrayList<String> listName;
    CircleImageView icon1,icon2, icon3, icon4, icon5, icon6, icon7, icon8;
    int idIconSource = R.drawable.ic_new_list;

    public static DialogCreateNewList newInstance() {
        DialogCreateNewList frag = new DialogCreateNewList();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public interface OnCreateListListener{
        void OnButtonClick(String name, int idIconSource);
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
        edtName = (EditText) rootView.findViewById(R.id.edtName);
        icon1 = (CircleImageView) rootView.findViewById(R.id.icon1);
        icon2 = (CircleImageView) rootView.findViewById(R.id.icon2);
        icon3 = (CircleImageView) rootView.findViewById(R.id.icon3);
        icon4 = (CircleImageView) rootView.findViewById(R.id.icon4);
        icon5 = (CircleImageView) rootView.findViewById(R.id.icon5);
        icon6 = (CircleImageView) rootView.findViewById(R.id.icon6);
        icon7 = (CircleImageView) rootView.findViewById(R.id.icon7);
        icon8 = (CircleImageView) rootView.findViewById(R.id.icon8);
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
        listName = new ArrayList<>();
        listName.add("My Save Places");
        listName.add("My Favourite Places");
        listName.add("My Checked in Places");
        getIconSourceId();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtName.getText().length() < 1){
                    Toast.makeText(getContext(),"Name is not empty!",Toast.LENGTH_SHORT).show();
                }
                else {
                    boolean check = true;
                    for (int i = 0; i< listName.size();i++){
                        if (edtName.getText().equals(listName.get(i))){
                            Toast.makeText(getContext(),"Name is already created, try a new one!",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                    }
                    if (check)
                    {
                        mListener.OnButtonClick(edtName.getText().toString(), idIconSource);
                        listName.add(edtName.getText().toString());
                        dismiss();
                    }
                }
            }
        });


    }

    public void getIconSourceId(){
        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_samplelist1;
            }
        });
        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_samplelist2;
            }
        });
        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_samplelist3;
            }
        });
        icon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_samplelist4;
            }
        });
        icon5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_samplelist5;
            }
        });
        icon6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_samplelist6;
            }
        });
        icon7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_samplelist7;
            }
        });
        icon8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_samplelist8;
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
