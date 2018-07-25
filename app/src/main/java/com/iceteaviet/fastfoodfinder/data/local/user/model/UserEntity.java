package com.iceteaviet.fastfoodfinder.data.local.user.model;

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Genius Doan on 2018.
 */
public class UserEntity extends RealmObject {

    private String name;
    private String email;
    private String uid;
    private String photoUrl;
    private RealmList<UserStoreListEntity> userStoreListEntities;

    public void map(User user) {
        name = user.getName();
        email = user.getEmail();
        uid = user.getUid();
        photoUrl = user.getPhotoUrl();
        this.userStoreListEntities = new RealmList<>();
        List<UserStoreList> userStoreLists = user.getUserStoreLists();
        for (int i = 0; i < userStoreLists.size(); i++) {
            this.userStoreListEntities.add(new UserStoreListEntity(userStoreLists.get(i)));
        }
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

    public RealmList<UserStoreListEntity> getUserStoreLists() {
        return userStoreListEntities;
    }

    public void setUserStoreLists(RealmList<UserStoreListEntity> userStoreLists) {
        this.userStoreListEntities = userStoreLists;
    }
}
