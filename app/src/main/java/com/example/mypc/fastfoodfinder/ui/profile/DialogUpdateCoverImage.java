package com.example.mypc.fastfoodfinder.ui.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mypc.fastfoodfinder.R;

/**
 * Created by MyPC on 11/29/2016.
 */
public class DialogUpdateCoverImage extends android.support.v4.app.DialogFragment {

    ImageView ivOne;
    ImageView ivTwo;
    ImageView ivThree;
    ImageView ivFour;
    ImageView ivFive;
    ImageView ivSix;
    ImageView ivChosenImage;
    Button btnDone;
    Button btnCancel;
    int IdChosenImage = 0;
    OnButtonClickListener mListener;


    public interface OnButtonClickListener{
        void onButtonClickListener(int Id);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener){
        mListener = listener;
    }

    public DialogUpdateCoverImage(){}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_choose_image,container,false);
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
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivOne = (ImageView) view.findViewById(R.id.ivOne);
        ivTwo = (ImageView) view.findViewById(R.id.ivTwo);
        ivThree = (ImageView) view.findViewById(R.id.ivThree);
        ivFour = (ImageView) view.findViewById(R.id.ivFour);
        ivFive = (ImageView) view.findViewById(R.id.ivFive);
        ivSix = (ImageView) view.findViewById(R.id.ivSix);
        ivChosenImage = (ImageView) view.findViewById(R.id.ivChosenImage);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnDone = (Button) view.findViewById(R.id.btnDone);
        ivOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.mot);
                IdChosenImage = R.drawable.mot;
            }
        });
        ivTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.hai);
                IdChosenImage = R.drawable.hai;

            }
        });
        ivThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.ba);
                IdChosenImage = R.drawable.ba;
            }
        });
        ivFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.bon);
                IdChosenImage = R.drawable.bon;
            }
        });
        ivFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.nam);
                IdChosenImage = R.drawable.nam;
            }
        });
        ivSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.sau);
                IdChosenImage = R.drawable.sau;
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClickListener(IdChosenImage);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
    public static DialogUpdateCoverImage newInstance() {
        DialogUpdateCoverImage frag = new DialogUpdateCoverImage();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

}
