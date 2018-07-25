package com.iceteaviet.fastfoodfinder.data.local.user.model;

import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by tom on 7/26/18.
 */
public class UserStoreListEntity extends RealmObject {
    private int id;
    private String listName;
    private int iconId;
    private RealmList<Integer> storeIdList;

    public UserStoreListEntity() {
        // Realm required
    }

    public UserStoreListEntity(UserStoreList userStoreList) {
        map(userStoreList);
    }

    public void map(UserStoreList userStoreList) {
        id = userStoreList.getId();
        listName = userStoreList.getListName();
        iconId = userStoreList.getIconId();
        storeIdList = new RealmList<>();
        storeIdList.addAll(userStoreList.getStoreIdList());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public RealmList<Integer> getStoreIdList() {
        return storeIdList;
    }

    public void setStoreIdList(RealmList<Integer> storeIdList) {
        this.storeIdList = storeIdList;
    }
}
