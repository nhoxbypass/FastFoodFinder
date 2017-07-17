package com.iceteaviet.fastfoodfinder.model.User;

import android.util.Log;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.model.Store.UserStoreList;
import com.iceteaviet.fastfoodfinder.rest.FirebaseClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhoxb on 11/24/2016.
 */
public class User {

    public static User currentUser;
    private String name;
    private String email;
    private String uid;
    private String photoUrl;
    private List<UserStoreList> userStoreLists;

    public User() {
    }

    public User(String name, String email, String photoUrl, String uid, List<UserStoreList> storeLists) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;

        if (storeLists == null || storeLists.size() == 0) {
            this.userStoreLists = new ArrayList<>();

            List<Integer> storeIdList = new ArrayList<>();
            storeIdList.add(5);
            storeIdList.add(25);
            storeIdList.add(115);
            storeIdList.add(255);
            storeIdList.add(344);
            userStoreLists.add(new UserStoreList(0, storeIdList, R.drawable.ic_profile_saved, "My Saved Places"));

            storeIdList = new ArrayList<>();
            storeIdList.add(5);
            storeIdList.add(115);
            storeIdList.add(344);

            userStoreLists.add(new UserStoreList(1, storeIdList, R.drawable.ic_profile_favourite, "My Favourite Places"));
            userStoreLists.add(new UserStoreList(2, storeIdList, R.drawable.ic_profile_checkin, "My Checked in Places"));
        } else
            this.userStoreLists = storeLists;
    }

    public UserStoreList getFavouriteStoreList() {
        for (int i = 0; i < userStoreLists.size(); i++) {
            if (userStoreLists.get(i).getId() == UserStoreList.ID_FAVOURITE) {
                return userStoreLists.get(i);
            }
        }

        Log.wtf(User.class.getName(), "Cannot find favourite list !!!");
        return null;
    }

    public List<UserStoreList> getUserStoreLists() {
        return userStoreLists;
    }

    public void setUserStoreLists(List<UserStoreList> userStoreLists) {
        this.userStoreLists = userStoreLists;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void addStoreList(UserStoreList list) {
        userStoreLists.add(list);
        FirebaseClient.getInstance().saveUserStoreListIfNotExists(this.uid, userStoreLists);
    }

    public void removeStoreList(int position) {
        userStoreLists.remove(position);
        FirebaseClient.getInstance().saveUserStoreListIfNotExists(uid, userStoreLists);
    }


    //TODO: Save user data to Firebase. Can extense to ic_profile_saved other attribute like address, avatar image link
    public void setUserData() {
        FirebaseClient.getInstance().setUserData(new User(name, email, photoUrl, uid, userStoreLists));
    }

    public void saveUserIfNotExists() {
        FirebaseClient.getInstance().saveUserIfNotExists(new User(name, email, photoUrl, uid, userStoreLists));
    }
}
