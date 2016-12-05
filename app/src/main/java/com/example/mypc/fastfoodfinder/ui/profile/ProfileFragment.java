package com.example.mypc.fastfoodfinder.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.example.mypc.fastfoodfinder.activity.DetailListActivity;
import com.example.mypc.fastfoodfinder.adapter.ListPacketAdapter;
import com.example.mypc.fastfoodfinder.dialog.DialogCreateNewList;
import com.example.mypc.fastfoodfinder.model.ListPacket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "idIcon";
    public static final String KEY_NUMBER_PLACES ="number";
    public static final String KEY_URL ="url";
    @BindView(R.id.ivCoverImage)  ImageView ivCoverImage;
    @BindView(R.id.btnUpdateCoverImage)  Button btnUpdateCoverImage;
    @BindView(R.id.ivCreate)  CircleImageView civCreate;
     @BindView(R.id.cvCreateNew)   CardView cvCreate;
    @BindView(R.id.iv_profile_avatar)   ImageView ivAvatarProfile;
    @BindView(R.id.tvName)   TextView tvName;
    @BindView(R.id.tvEmail)   TextView tvEmail;
    @BindView(R.id.rvListPacket)  RecyclerView rvListPacket;
    @BindView(R.id.tvNumberList) TextView tvNumberList;
    @BindView(R.id.cv_saved_places) CardView cvSavePlace;
    @BindView(R.id.cv_checkin_places) CardView cvCheckinPlace;
    @BindView(R.id.cv_favourite_places) CardView cvFavouritePlace;
    DialogUpdateCoverImage mDialog;
    DialogCreateNewList mDialogCreate;
    public static ArrayList<String> listName;
    ListPacketAdapter mAdapter;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    StaggeredGridLayoutManager mLayoutManager;

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
<<<<<<< HEAD
        ivCoverImage = (ImageView) rootView.findViewById(R.id.ivCoverImage);
        btnUpdateCoverImage = (Button) rootView.findViewById(R.id.btnUpdateCoverImage);
        civCreate = (CircleImageView) rootView.findViewById(R.id.ivCreate);
        viewpoint = (CardView) rootView.findViewById(R.id.viewPoint);
        viewpoint2 = (CardView) rootView.findViewById(R.id.viewPoint2);
        viewpoint3 = (CardView) rootView.findViewById(R.id.viewpoint3);
        viewpoint4 = (CardView) rootView.findViewById(R.id.viewPoint4);
        viewpoint5 = (CardView) rootView.findViewById(R.id.viewpoint5);
        cvCreate = (CardView) rootView.findViewById(R.id.cvCreateNew);
        ivAvatarProfile = (ImageView) rootView.findViewById(R.id.iv_profile_avatar);
        tvName = (TextView) rootView.findViewById(R.id.tvName);
        tvEmail = (TextView) rootView.findViewById(R.id.tvEmail);
        ivCreateNew = (ImageView) rootView.findViewById(R.id.ivCreate);

=======
        ButterKnife.bind(this, rootView);
        listName = new ArrayList<>();
>>>>>>> origin/master
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listName.add("My Save Places");
        listName.add("My Favourite Places");
        listName.add("My Checked in Places");

        mAdapter = new ListPacketAdapter();
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        rvListPacket.setAdapter(mAdapter);
        rvListPacket.setLayoutManager(mLayoutManager);
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
                mDialogCreate.show(getFragmentManager(), "");
                mDialogCreate.setOnButtonClickListener(new DialogCreateNewList.OnCreateListListener() {
                    @Override
                    public void OnButtonClick(String name, int idIconSource) {
                        mAdapter.addListPacket(new ListPacket(name, idIconSource));
                        tvNumberList.setText("("+String.valueOf(mAdapter.getItemCount())+")");
                    }
                });
            }
        });

        cvSavePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailListActivity.class);
                intent.putExtra(KEY_NAME,"My Save Places");
                intent.putExtra(KEY_ID,R.drawable.ic_save);
                intent.putExtra(KEY_NUMBER_PLACES,2);
                intent.putExtra(KEY_URL,mFirebaseUser.getPhotoUrl());
                startActivity(intent);
            }
        });
        cvFavouritePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailListActivity.class);
                intent.putExtra(KEY_NAME,"My Favourite Places");
                intent.putExtra(KEY_ID,R.drawable.ic_favourite);
                intent.putExtra(KEY_NUMBER_PLACES,2);
                intent.putExtra(KEY_URL,mFirebaseUser.getPhotoUrl());

                startActivity(intent);
            }
        });
        cvCheckinPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailListActivity.class);
                intent.putExtra(KEY_NAME,"My Checked In Places");
                intent.putExtra(KEY_ID,R.drawable.ic_list_checkin);
                intent.putExtra(KEY_NUMBER_PLACES,2);
                intent.putExtra(KEY_URL,mFirebaseUser.getPhotoUrl());
                startActivity(intent);
            }
        });

        mAdapter.setOnItemClickListener(new ListPacketAdapter.OnItemClickListener() {
            @Override
            public void OnClick(ListPacket listPacket) {
                Intent intent = new Intent(getContext(), DetailListActivity.class);
                intent.putExtra(KEY_NAME,listPacket.getName());
                intent.putExtra(KEY_ID,listPacket.getIdIconSource());
                intent.putExtra(KEY_NUMBER_PLACES, 0);
                intent.putExtra(KEY_URL,mFirebaseUser.getPhotoUrl());
                startActivity(intent);
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
