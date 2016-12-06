package com.example.mypc.fastfoodfinder.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.dialog.DialogCreateNewList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    @BindView(R.id.ivCoverImage)  ImageView ivCoverImage;
    @BindView(R.id.btnUpdateCoverImage)  Button btnUpdateCoverImage;
    @BindView(R.id.ivCreate)  CircleImageView civCreate;
    @BindView(R.id.viewPoint)   CardView viewpoint;
    @BindView(R.id.viewPoint2)   CardView viewpoint2;
    @BindView(R.id.viewpoint3)   CardView viewpoint3;
    @BindView(R.id.viewPoint4)   CardView viewpoint4;
    @BindView(R.id.viewpoint5)   CardView viewpoint5;
    @BindView(R.id.cvCreateNew)   CardView cvCreate;
    @BindView(R.id.iv_profile_avatar)   ImageView ivAvatarProfile;
    @BindView(R.id.tvName)   TextView tvName;
    @BindView(R.id.tvEmail)   TextView tvEmail;
    static int listCount = 3;
    CircleImageView iconNew;
    DialogUpdateCoverImage mDialog;
    DialogCreateNewList mDialogCreate;


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    public static ProfileFragment newInstance(){
        Bundle extras = new Bundle();
       ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(extras);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile,container,false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            tvName.setText("Unregistered User");
            tvEmail.setText("anonymous@fastfoodfinder.com");
        } else {
            Glide.with(getContext())
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(ivAvatarProfile);
            tvName.setText(mFirebaseUser.getDisplayName());
            tvEmail.setText(mFirebaseUser.getEmail())   ;
        }

        ivCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUpdateCoverImage.setVisibility(View.VISIBLE);
            }
        });

        btnUpdateCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(mDialog);
                btnUpdateCoverImage.setVisibility(View.GONE);
            }
        });

        civCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mDialogCreate = DialogCreateNewList.newInstance();
                mDialogCreate.show(getFragmentManager(),"");
                mDialogCreate.setOnButtonClickListener(new DialogCreateNewList.OnCreateListListener() {
                    @Override
                    public void OnButtonClick(String name,int idIconSource) {
                        LayoutInflater newCardView = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        CardView cardView = (CardView) newCardView.inflate(R.layout.cardview_new_list,null);
                        TextView tvName = (TextView) cardView.findViewById(R.id.tvNameList);
                        tvName.setText(name);
                        if (listCount==4) {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(475,378);
                            params.setMargins(50,1000,0,50);
                            cvCreate.setLayoutParams(params);
                            ViewGroup insertPoint = (ViewGroup) viewpoint;
                            insertPoint.addView(cardView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            iconNew = (CircleImageView) insertPoint.findViewById(R.id.iconNewList);
                            iconNew.setImageResource(idIconSource);
                        }
                        else if (listCount==5)
                        {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(475,378);
                            params.setMargins(580,1000,0,50);
                            cvCreate.setLayoutParams(params);
                            ViewGroup insertPoint = (ViewGroup) viewpoint2;
                            insertPoint.addView(cardView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            iconNew = (CircleImageView) insertPoint.findViewById(R.id.iconNewList);
                            iconNew.setImageResource(idIconSource);
                        }
                        else if (listCount == 6){
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(475,378);
                            params.setMargins(50,1420,0,50);
                            cvCreate.setLayoutParams(params);
                            ViewGroup insertPoint = (ViewGroup) viewpoint3;
                            insertPoint.addView(cardView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            iconNew = (CircleImageView) insertPoint.findViewById(R.id.iconNewList);
                            iconNew.setImageResource(idIconSource);
                        }
                        else if (listCount == 7){
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(475,378);
                            params.setMargins(580,1420,0,50);
                            cvCreate.setLayoutParams(params);
                            ViewGroup insertPoint = (ViewGroup) viewpoint4;
                            insertPoint.addView(cardView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            iconNew = (CircleImageView) insertPoint.findViewById(R.id.iconNewList);
                            iconNew.setImageResource(idIconSource);
                        }
                        else if (listCount ==8){
                            cvCreate.setVisibility(View.GONE);
                            ViewGroup insertPoint = (ViewGroup) viewpoint5;
                            insertPoint.addView(cardView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            iconNew = (CircleImageView) insertPoint.findViewById(R.id.iconNewList);
                            iconNew.setImageResource(idIconSource);
                        }
                    }

                });
                listCount++;
            }
        });
    }

    public void showDialog(DialogUpdateCoverImage dialog){
        dialog = DialogUpdateCoverImage.newInstance();
        dialog.show(getFragmentManager(),"");
        dialog.setOnButtonClickListener(new DialogUpdateCoverImage.OnButtonClickListener() {
            @Override
            public void onButtonClickListener(int Id) {
                if (Id!=0)
                    ivCoverImage.setImageResource(Id);
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

}
