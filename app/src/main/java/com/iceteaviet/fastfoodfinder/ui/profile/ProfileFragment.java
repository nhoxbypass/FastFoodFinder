package com.iceteaviet.fastfoodfinder.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static com.iceteaviet.fastfoodfinder.ui.profile.ListDetailActivity.KEY_USER_PHOTO_URL;
import static com.iceteaviet.fastfoodfinder.ui.profile.ListDetailActivity.KEY_USER_STORE_LIST;


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

    private DialogUpdateCoverImage mDialog;
    private DialogCreateNewList mDialogCreate;
    private UserStoreListAdapter mAdapter;
    private List<UserStoreList> defaultList;
    private ArrayList<String> listName;

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
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvListPacket.setAdapter(mAdapter);
        rvListPacket.setLayoutManager(mLayoutManager);

        tvName.setText(R.string.unregistered_user);
        tvEmail.setText(R.string.unregistered_email);
        if (dataManager.getCurrentUser() == null)
            dataManager.setCurrentUser(new User(getString(R.string.unregistered_user), getString(R.string.unregistered_email), Constant.NO_AVATAR_PLACEHOLDER_URL, "null", new ArrayList<UserStoreList>()));

        if (dataManager.isSignedIn()) {
            getCurrentUserData();
        }
    }

    public void loadUserList() {
        User currentUser = dataManager.getCurrentUser();
        for (int i = 0; i < currentUser.getUserStoreLists().size(); i++) {
            if (i <= 2) {
                defaultList.add(currentUser.getUserStoreLists().get(i));
            } else {
                mAdapter.addListPacket(currentUser.getUserStoreLists().get(i));
            }
        }
        tvNumberList.setText(String.format("(%s)", String.valueOf(mAdapter.getItemCount())));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

    private void getCurrentUserData() {
        dataManager.getRemoteUserDataSource().getUser(dataManager.getCurrentUserUid())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(User user) {
                        dataManager.setCurrentUser(user);
                        Glide.with(getContext())
                                .load(user.getPhotoUrl())
                                .into(ivAvatarProfile);
                        tvName.setText(user.getName());
                        tvEmail.setText(user.getEmail());
                        loadUserList();
                        for (int i = 0; i < user.getUserStoreLists().size(); i++) {
                            listName.add(user.getUserStoreLists().get(i).getListName());
                        }
                        tvFavItemsCount.setText(String.format(Locale.getDefault(), "%d nơi", user.getFavouriteStoreList().getStoreIdList().size()));
                        onListener();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
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
                mDialog = DialogUpdateCoverImage.newInstance();
                mDialog.setOnButtonClickListener(new DialogUpdateCoverImage.OnButtonClickListener() {
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
                mDialog.show(getFragmentManager(), "");

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
                    public void onButtonClick(String name, int idIconSource) {
                        User currentUser = dataManager.getCurrentUser();
                        int id = currentUser.getUserStoreLists().size(); //New id = current size
                        UserStoreList list = new UserStoreList(id, new ArrayList<Integer>(), idIconSource, name);
                        mAdapter.addListPacket(list);
                        currentUser.addStoreList(list);
                        dataManager.getRemoteUserDataSource().updateStoreListForUser(currentUser.getUid(), currentUser.getUserStoreLists());
                        tvNumberList.setText(String.format("(%s)", String.valueOf(mAdapter.getItemCount())));
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
                User currentUser = dataManager.getCurrentUser();
                tvNumberList.setText(String.format("(%s)", String.valueOf(mAdapter.getItemCount())));
                currentUser.removeStoreList(position);
                dataManager.getRemoteUserDataSource().updateStoreListForUser(currentUser.getUid(), currentUser.getUserStoreLists());
            }
        });

        mAdapter.setOnItemClickListener(new UserStoreListAdapter.OnItemClickListener() {
            @Override
            public void onClick(UserStoreList listPacket) {
                sendToDetailListActivity(listPacket);

            }
        });
    }


    private void sendToDetailListActivity(UserStoreList userStoreList) {
        Intent intent = new Intent(getContext(), ListDetailActivity.class);
        intent.putExtra(KEY_USER_PHOTO_URL, dataManager.getCurrentUser().getPhotoUrl());
        intent.putExtra(KEY_USER_STORE_LIST, userStoreList);
        startActivity(intent);
    }
}
