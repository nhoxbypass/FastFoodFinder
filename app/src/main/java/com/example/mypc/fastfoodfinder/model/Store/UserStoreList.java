package com.example.mypc.fastfoodfinder.model.Store;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhoxb on 12/6/2016.
 */
public class UserStoreList implements Parcelable {

    private int id;
    private String listName;
    private int iconId;
    private ArrayList<Integer> storeIdList;

    protected UserStoreList(Parcel in) {
        id = in.readInt();
        listName = in.readString();
        iconId = in.readInt();
    }

    public UserStoreList() {
    }

    public UserStoreList(int id, ArrayList<Integer> storesId, int iconId, String listName) {
        this.id = id;
        this.iconId = iconId;
        this.listName = listName;

        if (storesId == null)
            this.storeIdList =  new ArrayList<>();
        else
            this.storeIdList = storesId;
    }


    public static final Creator<UserStoreList> CREATOR = new Creator<UserStoreList>() {
        @Override
        public UserStoreList createFromParcel(Parcel in) {
            return new UserStoreList(in);
        }

        @Override
        public UserStoreList[] newArray(int size) {
            return new UserStoreList[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Integer> getStoreList() {
        return storeIdList;
    }

    public void setStoreList(ArrayList<Integer> storeList) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(listName);
        parcel.writeInt(iconId);
        parcel.writeList(storeIdList);
    }
}
