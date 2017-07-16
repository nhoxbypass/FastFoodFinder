package com.iceteaviet.fastfoodfinder.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.iceteaviet.fastfoodfinder.activity.ListDetailActivity;
import com.iceteaviet.fastfoodfinder.model.Store.UserStoreList;
import com.iceteaviet.fastfoodfinder.model.User.User;
import com.iceteaviet.fastfoodfinder.rest.FirebaseClient;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.adapter.UserStoreListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    public static final String KEY_IDICON = "idicon";
    public static final String KEY_STORE= "store";

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
    @BindView(R.id.fav_list_items_count) TextView tvFavItemsCount;
    DialogUpdateCoverImage mDialog;
    DialogCreateNewList mDialogCreate;
    public static ArrayList<String> listName;
    UserStoreListAdapter mAdapter;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    StaggeredGridLayoutManager mLayoutManager;
    List<UserStoreList> defaultList;

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
        defaultList = new ArrayList<>();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new UserStoreListAdapter();
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        rvListPacket.setAdapter(mAdapter);
        rvListPacket.setLayoutManager(mLayoutManager);
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        tvName.setText("Unregistered User");
        tvEmail.setText("anonymous@fastfoodfinder.com");
        if (User.currentUser == null)
            User.currentUser = new User("Unregistered User", "anonymous@fastfoodfinder.com", "http://cdn.builtlean.com/wp-content/uploads/2015/11/all_noavatar.png.png", "null" ,new ArrayList<UserStoreList>());

        if (mFirebaseUser != null) {
            getUserData(mFirebaseUser.getUid());
        }
    }

    public void loadUserList(){
        for (int i = 0; i< User.currentUser.getUserStoreLists().size();i++){
            if(i <= 2) {
                defaultList.add(User.currentUser.getUserStoreLists().get(i));
            }
            else {
                mAdapter.addListPacket(User.currentUser.getUserStoreLists().get(i));
            }
        }
        tvNumberList.setText("("+String.valueOf(mAdapter.getItemCount())+")");
    }

    public void showDialog(DialogUpdateCoverImage dialog){
        dialog = DialogUpdateCoverImage.newInstance();
        dialog.show(getFragmentManager(),"");
        dialog.setOnButtonClickListener(new DialogUpdateCoverImage.OnButtonClickListener() {
            @Override
            public void onButtonClickListener(int Id, Bitmap bitmap) {
                if (Id!=0)
                    if(Id == -1){
                        ivCoverImage.setImageBitmap(bitmap);
                    }
                else {
                        ivCoverImage.setImageResource(Id);
                    }
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
        FirebaseClient.getInstance().addListenerForSingleUserValueEvent(uid, new FirebaseClient.UserValueEventListener() {
            @Override
            public void onDataChange(User user) {
                User.currentUser = user;
                Glide.with(getContext())
                        .load(User.currentUser.getPhotoUrl())
                        .into(ivAvatarProfile);
                tvName.setText(User.currentUser.getName());
                tvEmail.setText(User.currentUser.getEmail());
                loadUserList();
                for (int i = 0; i< User.currentUser.getUserStoreLists().size();i++){
                    listName.add(User.currentUser.getUserStoreLists().get(i).getListName());
                }
                tvFavItemsCount.setText(User.currentUser.getFavouriteStoreList().getStoreIdList().size() + " nơi");
                onListener();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void onListener(){
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
                        int id = User.currentUser.getUserStoreLists().size();
                        UserStoreList list = new UserStoreList(id, new ArrayList<Integer>(),idIconSource, name);
                        mAdapter.addListPacket(list);
                        User.currentUser.addStoreList(list);
                        tvNumberList.setText("("+String.valueOf(mAdapter.getItemCount())+")");
                    }
                });
            }
        });

        cvSavePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToDetailListActivity(defaultList.get(UserStoreList.ID_SAVED));

            }
        });
        cvFavouritePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToDetailListActivity(defaultList.get(UserStoreList.ID_FAVOURITE));

            }
        });
        cvCheckinPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  sendToDetailListActivity(defaultList.get(UserStoreList.ID_CHECKED_IN));
            }
        });

        mAdapter.setOnItemLongClickListener(new UserStoreListAdapter.OnItemLongClickListener() {
            @Override
            public void onClick(int position) {
                tvNumberList.setText("("+String.valueOf(mAdapter.getItemCount())+")");
                User.currentUser.removeStoreList(position);
            }
        });

        mAdapter.setOnItemClickListener(new UserStoreListAdapter.OnItemClickListener() {
            @Override
            public void OnClick(UserStoreList listPacket) {
                sendToDetailListActivity(listPacket);

            }
        });
    }


    void sendToDetailListActivity(UserStoreList userStoreList){
        Intent intent = new Intent(getContext(), ListDetailActivity.class);
        intent.putExtra(KEY_URL, User.currentUser.getPhotoUrl());
        intent.putExtra(KEY_NAME,userStoreList.getListName());
        intent.putExtra(KEY_ID,userStoreList.getId());
        intent.putExtra(KEY_IDICON,userStoreList.getIconId());
        Bundle bundle = new Bundle();
        ArrayList<Integer> tmp = new ArrayList<>(userStoreList.getStoreIdList());
        bundle.putIntegerArrayList(KEY_STORE, tmp);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
