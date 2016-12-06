package com.example.mypc.fastfoodfinder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerTabStrip;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.ui.profile.ProfileFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MyPC on 11/30/2016.
 */
public class DialogCreateNewList extends DialogFragment {
    @BindView(R.id.ivQuit)   ImageView ivQuit;
    @BindView(R.id.btnDone)   Button btnDone;
    @BindView(R.id.edtName)   EditText edtName;
    @BindView(R.id.icon1)   CircleImageView icon1;
    @BindView(R.id.icon2)   CircleImageView icon2;
    @BindView(R.id.icon3)   CircleImageView icon3;
    @BindView(R.id.icon4)   CircleImageView icon4;
    @BindView(R.id.icon5)   CircleImageView icon5;
    @BindView(R.id.icon6)   CircleImageView icon6;
    @BindView(R.id.icon7)   CircleImageView icon7;
    @BindView(R.id.icon8)   CircleImageView icon8;
    @BindView(R.id.icon9)   CircleImageView icon9;
    @BindView(R.id.icon10)   CircleImageView icon10;
    OnCreateListListener mListener;
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
        ButterKnife.bind(this,rootView);
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
        getIdIconSource();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtName.getText().length() < 1){
                    Toast.makeText(getContext(),"Name is not empty!",Toast.LENGTH_SHORT).show();
                }
                else {
                    boolean check = true;
                    for (int i = 0; i< ProfileFragment.listName.size();i++){
                        if (edtName.getText().equals(ProfileFragment.listName.get(i))){
                            Toast.makeText(getContext(),"Name is already created, try a new one!",Toast.LENGTH_SHORT).show();
                            check = false;
                        }
                    }
                    if (check)
                    {
                        mListener.OnButtonClick(edtName.getText().toString(), idIconSource);
                        ProfileFragment.listName.add(edtName.getText().toString());
                        dismiss();
                    }
                }
            }
        });


    }

    public void getIdIconSource(){
        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist1;
                icon1.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));
            }
        });
        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist2;
                icon2.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));

            }
        });
        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist3;
                icon3.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));

            }
        });
        icon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist4;
                icon4.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));

            }
        });
        icon5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist5;
                icon5.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));

            }
        });
        icon6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist6;
                icon6.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));

            }
        });
        icon7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist7;
                icon7.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));
            }
        });
        icon8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist8;
                icon8.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));

            }
        });
        icon9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist9;
                icon9.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));

            }
        });
        icon10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idIconSource = R.drawable.ic_newlist10;
                icon10.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.image_click));

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
