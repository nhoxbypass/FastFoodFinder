package com.iceteaviet.fastfoodfinder.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.iceteaviet.fastfoodfinder.R;

import java.io.FileDescriptor;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MyPC on 11/29/2016.
 */
public class DialogUpdateCoverImage extends android.support.v4.app.DialogFragment {

    private static int RESULT_LOAD_IMAGE = 1;


    @BindView(R.id.ivOne)
    ImageView ivOne;
    @BindView(R.id.ivTwo)
    ImageView ivTwo;
    @BindView(R.id.ivThree)
    ImageView ivThree;
    @BindView(R.id.ivFour)
    ImageView ivFour;
    @BindView(R.id.ivFive)
    ImageView ivFive;
    @BindView(R.id.ivSix)
    ImageView ivSix;
    @BindView(R.id.ivChosenImage)
    ImageView ivChosenImage;
    @BindView(R.id.btnDone)
    Button btnDone;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnBrowser)
    Button btnBrowser;
    int IdChosenImage = 0;
    Bitmap mBmp = null;

    OnButtonClickListener mListener;


    public static DialogUpdateCoverImage newInstance() {
        DialogUpdateCoverImage frag = new DialogUpdateCoverImage();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_choose_image, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        btnBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        ivOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.profile_sample_background);
                IdChosenImage = R.drawable.profile_sample_background;
            }
        });
        ivTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.all_sample_avatar);
                IdChosenImage = R.drawable.all_sample_avatar;

            }
        });
        ivThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.profile_sample_background_3);
                IdChosenImage = R.drawable.profile_sample_background_3;
            }
        });
        ivFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.profile_sample_background_4);
                IdChosenImage = R.drawable.profile_sample_background_4;
            }
        });
        ivFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.profile_sample_background_5);
                IdChosenImage = R.drawable.profile_sample_background_5;
            }
        });
        ivSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivChosenImage.setImageResource(R.drawable.profile_sample_background_6);
                IdChosenImage = R.drawable.profile_sample_background_6;
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClickListener(IdChosenImage, mBmp);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivChosenImage.setImageBitmap(bmp);
            IdChosenImage = -1;
            mBmp = bmp;
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public interface OnButtonClickListener {
        void onButtonClickListener(int Id, Bitmap bmp);
    }

}
