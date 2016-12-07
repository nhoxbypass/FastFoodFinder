package com.example.mypc.fastfoodfinder.model.Store;

import java.util.List;

/**
 * Created by nhoxb on 12/6/2016.
 */
public class UserStoreList {

    private int id;
    private String listName;
    private int iconId;
    private List<Integer> storeIdList;

    public UserStoreList() {
    }

    public UserStoreList(int id, List<Integer> storesId, int iconId, String listName) {
        this.id = id;
        this.storeIdList = storesId;
        this.iconId = iconId;
        this.listName = listName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getStoreList() {
        return storeIdList;
    }

    public void setStoreList(List<Integer> storeList) {
        this.storeIdList = storeList;
    }

    public void addStore(Store store) {
        storeIdList.add(store.getId());
    }

    public void addStore(int storeId) {
        storeIdList.add(storeId);
    }

    public void removeStore(int storeId) {
        storeIdList.remove(Integer.valueOf(storeId));
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
}
