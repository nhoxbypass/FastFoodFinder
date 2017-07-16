package com.iceteaviet.fastfoodfinder.model.Store;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhoxb on 12/6/2016.
 */
public class UserStoreList implements Parcelable {
    public static final int ID_SAVED = 0;
    public static final int ID_FAVOURITE = 1;
    public static final int ID_CHECKED_IN = 2;
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
    private int id;
    private String listName;
    private int iconId;
    @PropertyName("storeIdList")
    private List<Integer> storeIdList;

    protected UserStoreList(Parcel in) {
        id = in.readInt();
        listName = in.readString();
        iconId = in.readInt();
        storeIdList = new ArrayList<>();
        in.readList(storeIdList, Integer.class.getClassLoader());
    }

    public UserStoreList() {
    }


    public UserStoreList(int id, List<Integer> storeIdList, int iconId, String listName) {
        this.id = id;
        this.iconId = iconId;
        this.listName = listName;

        if (storeIdList == null)
            this.storeIdList = new ArrayList<>();
        else
            this.storeIdList = storeIdList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getStoreIdList() {
        return storeIdList;
    }

    @PropertyName("storeIdList")
    public void setStoreIdList(List<Integer> storeIdList) {
        this.storeIdList = storeIdList;
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
