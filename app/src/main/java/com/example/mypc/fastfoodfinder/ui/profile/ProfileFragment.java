package com.example.mypc.fastfoodfinder.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.activity.DetailListActivity;
import com.example.mypc.fastfoodfinder.adapter.UserStoreListAdapter;
import com.example.mypc.fastfoodfinder.dialog.DialogCreateNewList;
import com.example.mypc.fastfoodfinder.model.Store.UserStoreList;
import com.example.mypc.fastfoodfinder.model.User.User;
import com.example.mypc.fastfoodfinder.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    @BindView(R.id.ivCreate) CircleImageView civCreate;
    @BindView(R.id.cvCreateNew)  CardView cvCreate;
    @BindView(R.id.iv_profile_avatar)  ImageView ivAvatarProfile;
    @BindView(R.id.tvName)  TextView tvName;
    @BindView(R.id.tvEmail)  TextView tvEmail;
    @BindView(R.id.rvListPacket)  RecyclerView rvListPacket;
    @BindView(R.id.tvNumberList) TextView tvNumberList;
    @BindView(R.id.cv_saved_places) CardView cvSavePlace;
    @BindView(R.id.cv_checkin_places) CardView cvCheckinPlace;
    @BindView(R.id.cv_favourite_places) CardView cvFavouritePlace;
    DialogUpdateCoverImage mDialog;
    DialogCreateNewList mDialogCreate;
    public static ArrayList<String> listName;
    UserStoreListAdapter mAdapter;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    StaggeredGridLayoutManager mLayoutManager;

    private User mCurrUser;

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
        listName = new ArrayList<>();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listName.add("My Save Places");
        listName.add("My Favourite Places");
        listName.add("My Checked in Places");

        mAdapter = new UserStoreListAdapter();
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        rvListPacket.setAdapter(mAdapter);
        rvListPacket.setLayoutManager(mLayoutManager);
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            tvName.setText("Unregistered User");
            tvEmail.setText("anonymous@fastfoodfinder.com");
            mCurrUser = new User("Unregistered User", "anonymous@fastfoodfinder.com", "http://cdn.builtlean.com/wp-content/uploads/2015/11/noavatar.png", "null" ,new ArrayList<UserStoreList>());
        } else {
            getUserData(mFirebaseUser.getUid());
        }

        load();
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
                        int id = mCurrUser.getUserStoreLists().size();
                        mAdapter.addListPacket(new UserStoreList(id, new ArrayList<Integer>(),idIconSource, name));
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

        mAdapter.setOnItemClickListener(new UserStoreListAdapter.OnItemClickListener() {
            @Override
            public void OnClick(UserStoreList listPacket) {
                Intent intent = new Intent(getContext(), DetailListActivity.class);
                intent.putExtra(KEY_NAME,listPacket.getListName());
                intent.putExtra(KEY_ID,listPacket.getIconId());
                intent.putExtra(KEY_NUMBER_PLACES, 0);
                intent.putExtra(KEY_URL,mFirebaseUser.getPhotoUrl());
                startActivity(intent);
            }
        });
    }

    public void load(){
        for (int i = 0; i< mCurrUser.getUserStoreLists().size();i++){
            mAdapter.addListPacket(new UserStoreList(mCurrUser.getUserStoreLists().get(i).getId(), new ArrayList<Integer>(),mCurrUser.getUserStoreLists().get(i).getIconId(), mCurrUser.getUserStoreLists().get(i).getListName()));
        }
        tvNumberList.setText("("+String.valueOf(mAdapter.getItemCount())+")");
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

    void getUserData(String uid)
    {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference().child(Constant.CHILD_USERS).child(uid);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrUser = dataSnapshot.getValue(User.class);
                Glide.with(getContext())
                        .load(mCurrUser.getPhotoUrl())
                        .into(ivAvatarProfile);
                tvName.setText(mCurrUser.getName());
                tvEmail.setText(mCurrUser.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MAPP", "Failed to get user data");
            }
        });
    }

}
