package com.iceteaviet.fastfoodfinder.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iceteaviet.fastfoodfinder.App;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList;
import com.iceteaviet.fastfoodfinder.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;


public class ProfileFragment extends Fragment {
    @BindView(R.id.ivCoverImage)
    ImageView ivCoverImage;
    @BindView(R.id.btnUpdateCoverImage)
    Button btnUpdateCoverImage;
    @BindView(R.id.ivCreate)
    CircleImageView civCreate;
    @BindView(R.id.cvCreateNew)
    CardView cvCreate;
    @BindView(R.id.iv_profile_avatar)
    ImageView ivAvatarProfile;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.rvListPacket)
    RecyclerView rvListPacket;
    @BindView(R.id.tvNumberList)
    TextView tvNumberList;
    @BindView(R.id.cv_saved_places)
    CardView cvSavePlace;
    @BindView(R.id.cv_checkin_places)
    CardView cvCheckinPlace;
    @BindView(R.id.cv_favourite_places)
    CardView cvFavouritePlace;
    @BindView(R.id.fav_list_items_count)
    TextView tvFavItemsCount;
    DialogUpdateCoverImage mDialog;
    DialogCreateNewList mDialogCreate;
    UserStoreListAdapter mAdapter;
    StaggeredGridLayoutManager mLayoutManager;
    List<UserStoreList> defaultList;
    ArrayList<String> listName;

    private DataManager dataManager;

    public static ProfileFragment newInstance() {
        Bundle extras = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(extras);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        listName = new ArrayList<>();
        defaultList = new ArrayList<>();
        dataManager = App.getDataManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new UserStoreListAdapter();
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvListPacket.setAdapter(mAdapter);
        rvListPacket.setLayoutManager(mLayoutManager);

        tvName.setText("Unregistered User");
        tvEmail.setText("anonymous@fastfoodfinder.com");
        if (User.currentUser == null)
            User.currentUser = new User("Unregistered User", "anonymous@fastfoodfinder.com", "http://cdn.builtlean.com/wp-content/uploads/2015/11/all_noavatar.png.png", "null", new ArrayList<UserStoreList>());

        if (dataManager.isSignedIn()) {
            getUserData(dataManager.getCurrentUserUid());
        }
    }

    public void loadUserList() {
        for (int i = 0; i < User.currentUser.getUserStoreLists().size(); i++) {
            if (i <= 2) {
                defaultList.add(User.currentUser.getUserStoreLists().get(i));
            } else {
                mAdapter.addListPacket(User.currentUser.getUserStoreLists().get(i));
            }
        }
        tvNumberList.setText("(" + String.valueOf(mAdapter.getItemCount()) + ")");
    }

    public void showDialog(DialogUpdateCoverImage dialog) {
        dialog = DialogUpdateCoverImage.newInstance();
        dialog.show(getFragmentManager(), "");
        dialog.setOnButtonClickListener(new DialogUpdateCoverImage.OnButtonClickListener() {
            @Override
            public void onButtonClickListener(int Id, Bitmap bitmap) {
                if (Id != 0)
                    if (Id == -1) {
                        ivCoverImage.setImageBitmap(bitmap);
                    } else {
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

    void getUserData(String uid) {
        dataManager.getUserDataSource().getUser(uid)
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        User.currentUser = user;
                        Glide.with(getContext())
                                .load(User.currentUser.getPhotoUrl())
                                .into(ivAvatarProfile);
                        tvName.setText(User.currentUser.getName());
                        tvEmail.setText(User.currentUser.getEmail());
                        loadUserList();
                        for (int i = 0; i < User.currentUser.getUserStoreLists().size(); i++) {
                            listName.add(User.currentUser.getUserStoreLists().get(i).getListName());
                        }
                        tvFavItemsCount.setText(User.currentUser.getFavouriteStoreList().getStoreIdList().size() + " nơi");
                        onListener();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void onListener() {
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
                mDialogCreate = DialogCreateNewList.newInstance(listName);
                mDialogCreate.show(getFragmentManager(), "");
                mDialogCreate.setOnButtonClickListener(new DialogCreateNewList.OnCreateListListener() {
                    @Override
                    public void OnButtonClick(String name, int idIconSource) {
                        int id = User.currentUser.getUserStoreLists().size(); //New id = current size
                        UserStoreList list = new UserStoreList(id, new ArrayList<Integer>(), idIconSource, name);
                        mAdapter.addListPacket(list);
                        User.currentUser.addStoreList(list);
                        dataManager.getUserDataSource().updateStoreListForUser(User.currentUser.getUid(), User.currentUser.getUserStoreLists());
                        tvNumberList.setText("(" + String.valueOf(mAdapter.getItemCount()) + ")");
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
                tvNumberList.setText("(" + String.valueOf(mAdapter.getItemCount()) + ")");
                User.currentUser.removeStoreList(position);
                dataManager.getUserDataSource().updateStoreListForUser(User.currentUser.getUid(), User.currentUser.getUserStoreLists());
            }
        });

        mAdapter.setOnItemClickListener(new UserStoreListAdapter.OnItemClickListener() {
            @Override
            public void OnClick(UserStoreList listPacket) {
                sendToDetailListActivity(listPacket);

            }
        });
    }


    void sendToDetailListActivity(UserStoreList userStoreList) {
        Intent intent = new Intent(getContext(), ListDetailActivity.class);
        intent.putExtra(Constant.KEY_URL, User.currentUser.getPhotoUrl());
        intent.putExtra(Constant.KEY_NAME, userStoreList.getListName());
        intent.putExtra(Constant.KEY_ID, userStoreList.getId());
        intent.putExtra(Constant.KEY_IDICON, userStoreList.getIconId());
        Bundle bundle = new Bundle();
        ArrayList<Integer> tmp = new ArrayList<>(userStoreList.getStoreIdList());
        bundle.putIntegerArrayList(Constant.KEY_STORE, tmp);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
