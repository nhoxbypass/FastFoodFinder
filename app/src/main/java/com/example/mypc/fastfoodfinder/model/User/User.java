package com.example.mypc.fastfoodfinder.model.User;

import android.util.Log;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.model.Store.UserStoreList;
import com.example.mypc.fastfoodfinder.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhoxb on 11/24/2016.
 */
public class User {

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
            userStoreLists.add(new UserStoreList(0, new ArrayList<Integer>(), R.drawable.ic_save, "My Saved Places"));
            userStoreLists.add(new UserStoreList(1, new ArrayList<Integer>(), R.drawable.ic_favourite, "My Favourite Places"));
            userStoreLists.add(new UserStoreList(2, new ArrayList<Integer>(), R.drawable.ic_list_checkin, "My Checked in Places"));
        } else
            this.userStoreLists = storeLists;
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child(Constant.CHILD_USERS).child(uid);

        list.setId(userStoreLists.size());
        userStoreLists.add(list);

        ref.child("idList").setValue(userStoreLists);
    }

    public void removeStoreList(UserStoreList list) {
        userStoreLists.remove(list);
    }


    //TODO: Save user data to Firebase. Can extense to ic_save other attribute like address, avatar image link
    public void saveUserData(final DatabaseReference mFirebaseDatabaseReference) {
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(uid)) {
                    mFirebaseDatabaseReference.child(uid).setValue(new User(name, email, photoUrl, uid, userStoreLists));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MAPP", "Error checking user exists");
            }
        });
    }
}
